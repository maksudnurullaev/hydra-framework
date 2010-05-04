package org.hydra.db.beans;

import org.hydra.messages.handlers.CassandraMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.abstracts.ALogger;

public class AccessPath extends ALogger{
	private String _ksp;
	private String _cf;
	private String _ID;
	private String _key;
	
	public void setKsp(String inKsp) {
		this._ksp = inKsp;
	}
	public String getKsp() {
		return _ksp;
	}
	public void setCf(String inCf) {
		this._cf = inCf;
	}
	public String getCf() {
		return _cf;
	}
	public void setID(String inID) {
		this._ID = inID;
	}
	public String getID() {
		return _ID;
	}
	public void setKey(String inKey) {
		this._key = inKey;
	}
	public String getKey() {
		return _key;
	}
	
	public AccessPath(IMessage inMessage){
		String ksName   = inMessage.getData().get(CassandraMessageHandler._ksp_link);
		String cfName   = inMessage.getData().get(CassandraMessageHandler._cf_link);
		String scfKeyID = inMessage.getData().get(CassandraMessageHandler._ID_link);
		String cfKey    = inMessage.getData().get(CassandraMessageHandler._key_link);
		
		setKsp(ksName);
		setCf(cfName);
		setID(scfKeyID);
		setKey(cfKey);
	}
}
