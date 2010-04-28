package org.hydra.db.server.abstracts;

import java.util.Set;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraAccessor extends ALogger {

	private String host = null;
	private int port = -1;
	private TTransport _transport = null;
	private TProtocol _protocol = null;
	private String _cluster_name = null;
	private String _version = null;
	private Set<String> keyspaces = null;

	public ACassandraAccessor() {
		super();
	}

	/**
	 * @return the keyspaces
	 */
	public Set<String> getKeyspaces() {
		return keyspaces;
	}

	/**
	 * @param keyspaces the keyspaces to set
	 */
	public void setKeyspaces(Set<String> keyspaces) {
		this.keyspaces = keyspaces;
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

	public void setup() {
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
			setKeyspaces(client.recv_describe_keyspaces());
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

	public String getClusterName() {
		return _cluster_name;
	}

	public String getProtocolVersion() {
		return _version;
	}

}