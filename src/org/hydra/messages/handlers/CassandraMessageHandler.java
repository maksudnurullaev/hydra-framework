package org.hydra.messages.handlers;

import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;

public class CassandraMessageHandler extends AMessageHandler {
	public static final String _handler_name = "CassandraMessage";
	
	/* 1. WHAT   - db action
	   2. KIND   - ksname
	   3. cfname - cfname
	   4. cname  - cname
	*/ 
	public static final String _what_describe = "describe"; 
	public static final String _what_select = "select";     
	public static final String _what_insert = "insert";
	public static final String _what_delete = "delete";
	
	public static final String _cfname_key = "cfname";
	public static final String _cname_key = "cname";
	
	// HTML IDs
	public static final String _cfname_desc_divId = "_cfname_desc_div";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage,_cfname_key, _cname_key)) return inMessage;
				
		if(inMessage.getData().get(IMessage._data_what).equals(_what_describe)){
			String cfName = inMessage.getData().get(_cfname_key);         // cfName
			String ksName = inMessage.getData().get(IMessage._data_kind); // ksName
			String cName = inMessage.getData().get(_cname_key);           // cName
		
			getDBObjectDescription(inMessage, ksName, cfName, cName);
		}else{
			String errorStr = "error.unknown.what.action";
			inMessage.setError(errorStr);
		}
		
		return inMessage;				
	}

	private void getDBObjectDescription(IMessage inMessage, String ksName, String cfName, String cName) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		ACassandraDescriptorBean cdb = Constants.getCassandraServerDescriptor();
		
		inMessage.setHtmlContent(cdb.getAccessDescription(ksName, cfName, cName));		
	}
}
