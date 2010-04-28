package org.hydra.messages.handlers;

import org.hydra.db.beans.KSName;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.SessionManager;

public class CassandraMessageHandler extends AMessageHandler {
	public static final String _handler_name = "CassandraMessage";
	
	/* 1. WHAT   - db action
	   2. KIND   - ksname
	   3. cfname - cfname
	   4. cname  - cname
	*/ 
	public static final String _action = "action"; 
	public static final String _action_describe = "describe"; 
	public static final String _action_select = "select";     
	public static final String _action_insert = "insert";
	public static final String _action_delete = "delete";
	
	public static final String _ksname_key = "ksname";
	public static final String _cfname_key = "cfname";
	public static final String _cname_key = "cname";
	public static final String _ID = "ID";
	
	// HTML IDs
	public static final String _cfname_desc_divId = "_cfname_desc_div";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage,_action,_ksname_key,_cfname_key, _cname_key)) return inMessage;
				
		if(inMessage.getData().get(_action).equals(_action_describe)){		
			getDBObjectDescription(inMessage);
		}else if(inMessage.getData().get(_action).equals(_action_select)){		
			if(!isValidMessage(inMessage,_ID)) return inMessage;
			getDBObject(inMessage);
		}else {
			String errorStr = "error.unknown.what.action: " + inMessage.getData().get(_action);
			inMessage.setError(errorStr);
		}
		
		return inMessage;				
	}

	private void getDBObject(IMessage inMessage) {
		String format = Constants.getTemplate("template.html.Strongtext.Text.br", null);
		String result = String.format(format, _ksname_key, inMessage.getData().get(_ksname_key));
		result += String.format(format, _cfname_key, inMessage.getData().get(_cfname_key));
		result += String.format(format, _cname_key, inMessage.getData().get(_cname_key));
		result += String.format(format, _ID, inMessage.getData().get(_ID));

		String type = Constants.getCassandraServerDescriptor().getCName(
				inMessage.getData().get(_ksname_key), 
				inMessage.getData().get(_cfname_key),
				inMessage.getData().get(_cname_key)).getType().toString();
		
		result += String.format(format, "Culomn Type", type);
		
		
		inMessage.setHtmlContent(result);
	}

	private void getDBObjectDescription(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		ACassandraDescriptorBean cdb = Constants.getCassandraServerDescriptor();
		
		inMessage.setHtmlContent(cdb.getAccessDescription(
				inMessage.getData().get(_ksname_key),
				inMessage.getData().get(_cfname_key),
				inMessage.getData().get(_cname_key)));		
	}
}
