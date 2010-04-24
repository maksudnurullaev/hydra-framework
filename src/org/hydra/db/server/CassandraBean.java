package org.hydra.db.server;

//import org.hydra.utils.Result;
import java.util.Set;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.abstracts.ALogger;

public class CassandraBean extends ALogger {
	private String host = null;
	private int port = -1;
	
	private TTransport transport = null;
	private TProtocol protocol = null;
	private String _cluster_name = null;
	private Set<String> _keyspaces = null;
	private String _version;

	public void setTransport(TTransport transport) {
		if(transport != null)
			transport.close();
		this.transport = transport;
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
		String formatStrong = "%s<strong>%s:</strong> %s<br />";
		try{
			result.append(String.format(formatStrong,"","Cluster name",_cluster_name));
			result.append(String.format(formatStrong,"","Ip", getHost()));
			result.append(String.format(formatStrong,"","Port", getPort()));
			result.append(String.format(formatStrong,"","Version", _version));
			result.append(String.format(formatStrong,"","Keyspaces", getKSNamesJSLinks(_keyspaces)));
		}catch (Exception e) {
			result.append(e.getMessage());
			getLog().error(e.getMessage());
		}			
		return result.toString();
	}

	private String getKSNamesJSLinks(Set<String> inKSNameList){
		StringBuffer result = new StringBuffer();	
		int counter = 0;
		String ksNameBean;
		
		for (String KSname: inKSNameList) {
			if(counter++ != 0)
				result.append(", ");
			 ksNameBean = Constants._ksname_prefix + KSname;	
			if(AppContext.getApplicationContext().containsBean(ksNameBean))			
				result.append(String.format(Constants.getTemplate("template.html.a.onclick.label", null),
						Constants.getJStrSendMessage(
								AdminMessageHandler._handler_name,
								AdminMessageHandler._what_cassandra_ksname_desc,
								ksNameBean,
								AdminMessageHandler._defaultContentBodyIDTail), 
						KSname));
			else result.append(KSname);
			
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
			_keyspaces = client.recv_describe_keyspaces();
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


}
