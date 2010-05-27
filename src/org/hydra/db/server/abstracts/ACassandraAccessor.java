package org.hydra.db.server.abstracts;

import java.util.HashSet;
import java.util.Set;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.utils.Constants;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraAccessor extends ALogger {
	private String _host = null;
	private int _port = -1;
	private TTransport _transport = null;
	private TProtocol _protocol = null;
	private String _cluster_name = null;
	private String _version = null;
	private Set<String> _keyspaces = null;
	private CassandraDescriptorBean _descriptor = null;
		
	public CassandraDescriptorBean getDescriptor() {
		return _descriptor;
	}

	public void setDescriptor(CassandraDescriptorBean descriptor) {
		this._descriptor = descriptor;
	}

	// **** For Cassandra.Client pool functionality
	private Set<Cassandra.Client> _cassandraClientsActive = new HashSet<Cassandra.Client>();
	private Set<Cassandra.Client> _cassandraClientsPassive = new HashSet<Cassandra.Client>();

	private int poolSizeMin = 0;	
	
	public void setPoolSizeMin(int poolSizeMin) {
		getLog().debug("Set PoolSizeMin: " + poolSizeMin);
		this.poolSizeMin = poolSizeMin;
	}

	public int getPoolSizeMin() {
		return poolSizeMin;
	}	
	
	private void clientSetPassive(Cassandra.Client inClient){
		_cassandraClientsPassive.add(inClient);	
		if(_cassandraClientsActive.contains(inClient))
			_cassandraClientsActive.remove(inClient);
		
		getLog().debug(getPoolInfo());
	}
	
	private void clientSetActive(Cassandra.Client inClient){
		_cassandraClientsActive.add(inClient);
		if(_cassandraClientsPassive.contains(inClient))
			_cassandraClientsPassive.remove(inClient);
		
		getLog().debug(getPoolInfo());
	}
	
	public Cassandra.Client clientGet(){
		getLog().debug(getPoolInfo());
		if(_cassandraClientsPassive.size() == 0){
			getLog().warn("P(0) - create new one...");
			// 1. If NO passive clients exist, create new one 
			Cassandra.Client client = clientCreate();
			clientSetActive(client);
			getLog().debug(getPoolInfo());
			return client;
		}
		
		// 2. Else get 
		getLog().debug("Get passive client...");
		Cassandra.Client client = _cassandraClientsPassive.iterator().next();
		
		clientSetActive(client);
		
		getLog().debug("Remove client from passive pool...");
		_cassandraClientsPassive.remove(client);
		getLog().debug(getPoolInfo());
		
		return client;
	}	
	
	private String getPoolInfo(){
		return String.format("Pools size: A(%s)/P(%s)", 
				_cassandraClientsActive.size(),
				_cassandraClientsPassive.size());
	}
	
	public void clientClose(Cassandra.Client inClient){
		getLog().debug("Return client to passive pool...");
		clientSetPassive(inClient);
		getLog().debug(getPoolInfo());
	}
	
	private Client clientCreate() {
		getLog().debug("Create new cassandra client!");
		return (new Cassandra.Client(getProtocol()));
	}
	// ############################################################################################
	
	public ACassandraAccessor() {
		super();
	}

	/**
	 * @return the keyspaces
	 */
	public Set<String> getServerKeyspaces() {
		return _keyspaces;
	}

	/**
	 * @param keyspaces the keyspaces to set
	 */
	public void setKeyspaces(Set<String> keyspaces) {
		this._keyspaces = keyspaces;
	}

	public void setTransport(TTransport transport) {
		if(transport != null)
			transport.close();
		this._transport = transport;
	}

	public TTransport getTransport() {
		return _transport;
	}

	public void setProtocol(TProtocol protocol) {
		this._protocol = protocol;
	}

	public TProtocol getProtocol() {
		return _protocol;
	}

	public String getHost() {
		return _host;
	}

	public void setHost(String inHost) {
		this._host = inHost;
		getLog().debug("Set host to: " + getHost());
	}

	public int getPort() {
		return _port;
	}

	public void setPort(int inPort) {
		this._port = inPort;
		getLog().debug("Set port to: " + getPort());
	}

	public void setup() {
		// 1. Init transport
		setTransport(new TSocket(getHost(),getPort()));
		getLog().debug(String.format("(host/port): (%s/%s)", getHost(), getPort()));
		
		// 2. Init protocol
		setProtocol(new TBinaryProtocol(getTransport()));
		getLog().debug("New TBinaryProtocol created!");
		
		if(!getTransport().isOpen());
			try {
				getLog().debug("Try to setup connection with Cassandra...");
				getTransport().open();
			} catch (TTransportException e) {
				getLog().fatal(Constants._attention_str + e.getMessage());
				return;
			}
		// 3. Init values
		for (int i = 0; i < getPoolSizeMin(); i++){
			getLog().debug("Create initial pool size...");
			clientSetPassive(clientCreate());
		}
		getLog().debug(getPoolInfo());
		
		Cassandra.Client client = clientGet();
		//Cassandra.Client client = new Cassandra.Client(getProtocol());
		try {
			getLog().debug("Get cassandra info...");
			client.send_describe_cluster_name();
			_cluster_name = client.recv_describe_cluster_name();
			client.send_describe_keyspaces();
			setKeyspaces(client.recv_describe_keyspaces());
			client.send_describe_version();
			_version = client.recv_describe_version();
			clientClose(client);
			
		} catch (TException e) {
			getLog().fatal(Constants._attention_str + e.getMessage());
			return;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// 1. Proper close trasport
		if(getTransport() != null && getTransport().isOpen()){
			getLog().warn("Cassandra trasport still in use, we should flush & close it!.");
			getTransport().flush();
			getTransport().close();			
		}
		super.finalize();
	}

	public String getClusterName() {
		return _cluster_name;
	}

	public String getProtocolVersion() {
		return _version;
	}



}