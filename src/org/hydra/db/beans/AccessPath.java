package org.hydra.db.beans;

import org.hydra.db.beans.CFKey.TYPE;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.messages.handlers.CassandraMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.SessionManager;
import org.hydra.utils.abstracts.ALogger;

public class AccessPath extends ALogger{
	private String _ksName;
	private String _cfName;
	private String _scfKey;
	private String _cfKey;
	private String _cName;
	private String _value;
	private String[] _values;
	private TYPE _type;
	
	public void setKsName(String ksName) {
		this._ksName = ksName;
	}
	public String getKsName() {
		return _ksName;
	}
	public void setCfName(String cfName) {
		this._cfName = cfName;
	}
	public String getCfName() {
		return _cfName;
	}
	public void setScfKey(String scfKey) {
		this._scfKey = scfKey;
	}
	public String getScfKey() {
		return _scfKey;
	}
	public void setCfKey(String cfKey) {
		this._cfKey = cfKey;
	}
	public String getCfKey() {
		return _cfKey;
	}
	public void setCName(String cName) {
		this._cName = cName;
	}
	public String getCName() {
		return _cName;
	}
	public void setValue(String value) {
		this._value = value;
	}
	public String getValue() {
		return _value;
	}
	public void setValues(String[] values) {
		this._values = values;
	}
	public String[] getValues() {
		return _values;
	}	
	public AccessPath(IMessage inMessage){
		String ksName = inMessage.getData().get(CassandraMessageHandler._ksname_link);
		String cfName = inMessage.getData().get(CassandraMessageHandler._cfname_link);
		String cName = inMessage.getData().get(CassandraMessageHandler._cname_link);
		String ID = inMessage.getData().get(CassandraMessageHandler._scfkey_link);

		CassandraDescriptorBean cdb = SessionManager.getCassandraServerDescriptor();		
		
		setKsName(ksName);
		setScfKey(ID);
		setType(cdb.getCName(ksName, cfName, cName).getType());
		
		if(cdb.getCName(ksName, cfName, cName).getType() == TYPE.COLUMNS){
			setCfName(cfName);
			setCfKey(cdb.getCName(ksName, cfName, cName).getType().toString());
			setCName(cName);
		}else if(cdb.getCName(ksName, cfName, cName).getType() == TYPE.LINKS){
			setCfName(cdb.getKSName(ksName).getLinkTableName());
			setCfKey(cName);			
		}else{
			getLog().error("Unknown CFKey type: " + cdb.getCName(ksName, cfName, cName).getType().toString());
		}
	}
	public void setType(TYPE type) {
		this._type = type;
	}
	public TYPE getType() {
		return _type;
	}
}
