package org.hydra.messages.handlers;

import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;

public class CassandraMessageHandler extends AMessageHandler {
	public static final String _handler_name = "CassandraMessage";
	public static final String _what_cassandra_cfname_desc = "cassandra_cfname_desc";
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
		if(!isValidMessage(inMessage)) return inMessage;
				
		if(!inMessage.getData().containsKey(_cname_key)){
			inMessage.setError(trace + "error.i.dint.know.what.to.do");
			return inMessage;
		}
		
		String cfName = inMessage.getData().get(IMessage._data_kind); // cfName
		String ksName = inMessage.getData().get(IMessage._data_what); // ksName
		String cName = inMessage.getData().get(_cname_key);           // cName
		
		getDBObjectDescription(inMessage, ksName, cfName, cName);
		
		return inMessage;				
	}

	private void getDBObjectDescription(IMessage inMessage, String ksName, String cfName, String cName) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		CassandraDescriptorBean cdb = Constants.getCassandraServerDescriptor();
		
		inMessage.setHtmlContent(cdb.getAccessDescription(ksName, cfName, cName));		
	}
}
