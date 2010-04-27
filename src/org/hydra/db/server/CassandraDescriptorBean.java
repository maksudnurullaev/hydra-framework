package org.hydra.db.server;

//import org.hydra.utils.Result;
import java.util.HashSet;
import java.util.Set;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hydra.db.beans.CFKey;
import org.hydra.db.beans.CFName;
import org.hydra.db.beans.KSName;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.abstracts.ALogger;

public class CassandraDescriptorBean extends ALogger {
	private String host = null;
	private int port = -1;
	
	private TTransport transport = null;
	private TProtocol protocol = null;
	private String _cluster_name = null;
	private String _version;
	private Set<KSName> _keyspaceBeans = new HashSet<KSName>();
	private Set<String> _keyspacesFromServer;
	
	public void setKeyspaces(Set<KSName> inKeyspaces){
		_keyspaceBeans = inKeyspaces;
	}

	public Set<KSName> getKeyspaces(){
		return _keyspaceBeans;
	}
	
	public void setTransport(TTransport transport) {
		if(transport != null)
			transport.close();
		this.transport = transport;
	}
	
	public KSName getKSName(String inKSName){
		for(KSName entry:getKeyspaces()){
			if(entry.getName().equals(inKSName))
				return entry;
		}
		return null;
	}

	public TTransport getTransport() {
		return transport;
	}

	public void setProtocol(TProtocol protocol) {
		this.protocol = protocol;
	}

	public TProtocol getProtocol() {
		return protocol;
	}	
	
	public String getHost() {
		return host;
	}

	public void setHost(String inHost) {
		this.host = inHost;
		getLog().debug("Set host to: " + getHost());
	}

	public int getPort() {
		return port;
	}

	public void setPort(int inPort) {
		this.port = inPort;
		getLog().debug("Set port to: " + getPort());
	}	
	
	public String getHTMLReport(){		
		StringBuffer result = new StringBuffer();
		String formatStrong = Constants.getTemplate("template.html.Strongtext.Text.br", null);
		try{
			result.append(String.format(formatStrong,"Cluster name",_cluster_name));
			result.append(String.format(formatStrong,"Ip", getHost()));
			result.append(String.format(formatStrong,"Port", getPort()));
			result.append(String.format(formatStrong,"Version", _version));
			result.append(String.format(formatStrong,"Keyspaces", getKSNamesJSLinks()));
			result.append(String.format(Constants.getTemplate("template.html.hr.divId.dots",null), KSName._ksname_desc_divId));
		}catch (Exception e) {
			result.append(e.getMessage());
			getLog().error(e.getMessage());
		}			
		return result.toString();
	}

	private String getKSNamesJSLinks(){
		StringBuffer result = new StringBuffer();	
		int counter = 0;
		
		for (KSName entryKSName: _keyspaceBeans) {
			if(_keyspacesFromServer.contains(entryKSName.getName())){
				if(counter++ != 0)
					result.append(", ");
					result.append(Constants.makeJSLink(entryKSName.getName(), 
							"handler:'%s',what:'%s',kind:'%s', dest:'%s'", 
							AdminMessageHandler._handler_name,
							AdminMessageHandler._what_cassandra_ksname_desc,
							entryKSName.getName(),
							KSName._ksname_desc_divId));
					
			}else result.append(String.format("Keyspace(%s) doesn't exist on server!", entryKSName.getName()));
		}
		
		return result.toString();
	}

	public void setupServer() {
		// 1. Init transport
		setTransport(new TSocket(getHost(),getPort()));
		getLog().warn(String.format("New cassandra trasport(host/port) setted up to: (%s/%s)", getHost(), getPort()));
		
		// 2. Init protocol
		setProtocol(new TBinaryProtocol(getTransport()));
		getLog().warn(String.format("New protocol initialized"));
		
		if(!getTransport().isOpen());
			try {
				getTransport().open();
			} catch (TTransportException e1) {
				getLog().error(e1.getMessage());
			}
		// 3.
		Cassandra.Client client = new Cassandra.Client(getProtocol());
		try {
			client.send_describe_cluster_name();
			_cluster_name = client.recv_describe_cluster_name();
			client.send_describe_keyspaces();
			_keyspacesFromServer = client.recv_describe_keyspaces();
			client.send_describe_version();
			_version = client.recv_describe_version();
			
		} catch (TException e) {
			getLog().error(e.getMessage());
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		// 1. Proper close trasport
		if(getTransport() != null && getTransport().isOpen()){
			getLog().warn("Cassandra trasport still in use, we sould flush & close it before...");
			getTransport().flush();
			getTransport().close();			
		}
		super.finalize();
	}

	public boolean checkDescriptions(String ksName, String cfName, String cName) {
		// 1. check ksname
		KSName ksNameBean = getKSName(ksName);
		if(ksNameBean == null) return false;
		
		// 2. check cfname
		CFName cfNameBean = ksNameBean.getCFName(cfName);
		if(cfName == null) return false;
		
		// 3. check cname
		return cfNameBean.getFields().containsKey(cName);
	}

	public String getAccessDescription(String ksName, String cfName, String cName) {
		String formatStrong = Constants.getTemplate("template.html.Strongtext.Text.br", null);
		String _access_format = String.format(formatStrong, "Column", "%s.%s['%s']['%s']['%s']");
		
		String inputBoxID = ksName + cfName;
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		if(!checkDescriptions(ksName, cfName, cName)){
			return trace + "error.ksname.cfname.cname.not.exist";
		}
		
		return String.format(_access_format, 
				ksName,
				cfName,
				String.format(Constants.getTemplate("template.html.custom.input.ID.Value", null), inputBoxID, inputBoxVal),
				getCName(ksName, cfName, cName).getType(),
				Constants.makeJSLink(cName, 
						"handler:'%s',what:'%s',kind:'%s',cname:'%s',dest:'%s'", 
						"CassandraData",
						ksName,
						cfName,
						cName,
						resultDivID)
				)
				+
				String.format(Constants.getTemplate("template.html.hr.divId.dots",null), resultDivID);
	}
	
	public CFKey getCName(String ksName, String cfName, String cName) {
		return getKSName(ksName).getCFName(cfName).getFields().get(cName);
	}	

}
