package org.hydra.messages.handlers;

import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.hydra.db.beans.AccessPath;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.SessionManager;

public class CassandraMessageHandler extends AMessageHandler {
	public static final String _handler_name = "CassandraMessage";
	public static final CassandraAccessorBean _cassandraAccessor = SessionManager.getCassandraServerAccessor();
	
	
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
	
	public static final String _ksp_link = "ksp";
	public static final String _cf_link  = "cf";
	public static final String _ID_link  = "ID";
	public static final String _key_link = "key";
	
	// HTML IDs
	public static final String _cf_desc_divId = "_cf_desc_div";
	
	

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage,_action,_ksp_link,_cf_link)){
			getLog().error("Invalid message!");
			return inMessage;
		}
				
		if(inMessage.getData().get(_action).equals(_action_describe)){
			getLog().debug("Describe db object");
			getDBObjectDescription(inMessage);
		}else if(inMessage.getData().get(_action).equals(_action_select)){		
			getLog().debug("Select db object");
			selectColumnValue(inMessage);
		}else {
			String errorStr = "error.unknown.what.action: " + inMessage.getData().get(_action);
			inMessage.setError(errorStr);
		}
		
		return inMessage;				
	}

	private void selectColumnValue(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		AccessPath accessPath = new AccessPath(inMessage);
		
		String format = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		List<Column> columns = _cassandraAccessor.getColumns(accessPath);
		
		if(columns == null){
			inMessage.setError("No column found");
			return;				
		}
		
		String resultStr = "";
		for (Column column:columns) {
			resultStr += String.format(format, "_name_", Constants.bytes2UTF8String(column.name, 32));				
			resultStr += String.format(format, "_value_", Constants.bytes2UTF8String(column.value, 32));				
			resultStr += String.format(format, "_timestamp_", column.timestamp);
		}
		
		inMessage.setHtmlContent(resultStr);
	}

	private void getDBObjectDescription(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// 3. create html context		
		inMessage.setHtmlContent(_cassandraAccessor.getDescriptor().getAccessDescription(new AccessPath(inMessage)));		
	}

}
