package org.hydra.messages.handlers;

import java.util.List;
import org.apache.cassandra.thrift.Column;
import org.hydra.db.beans.AccessPath;
import org.hydra.db.beans.CFKey.TYPE;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.SessionManager;

public class CassandraMessageHandler extends AMessageHandler {
	public static final String _handler_name = "CassandraMessage";
	public static final CassandraDescriptorBean cdb = SessionManager.getCassandraServerDescriptor();
	
	
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
	
	public static final String _ksname_link = "ksname";
	public static final String _cfname_link = "cfname";
	public static final String _scfkey_link = "scfname";
	public static final String _cfkey_link = "cfkey";
	public static final String _cname_link = "cname";
	
	// HTML IDs
	public static final String _cfname_desc_divId = "_cfname_desc_div";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage,_action,_ksname_link,_cfname_link, _cname_link)) return inMessage;
				
		if(inMessage.getData().get(_action).equals(_action_describe)){		
			getDBObjectDescription(inMessage);
		}else if(inMessage.getData().get(_action).equals(_action_select)){		
			if(!isValidMessage(inMessage,_scfkey_link)) return inMessage;
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
		String resultStr = String.format(format, _ksname_link, accessPath.getKsName());
		resultStr += String.format(format, _cfname_link, accessPath.getCfName());
		resultStr += String.format(format, _scfkey_link, accessPath.getScfKey());
		resultStr += String.format(format, _cfkey_link, accessPath.getCfKey());
		resultStr += String.format(format, _cname_link, accessPath.getCName());
		resultStr += String.format(format, "_type_", accessPath.getType());
		
		getLog().debug("Try to get columns: " + 
				(accessPath.getType() == TYPE.COLUMNS?accessPath.getCName():accessPath.getCfKey()));
		List<Column> columns = cdb.getAccessor().getColumns(accessPath);
		if(columns == null){
			inMessage.setError(accessPath.getValue());
			return;				
		}
		for (Column column:columns) {
			resultStr += String.format(format, "&nbsp;_timestamp_", column.timestamp);
			resultStr += String.format(format, "&nbsp;_value_", Constants.bytes2UTF8String(column.value, 32));				
		}
		
		inMessage.setHtmlContent(resultStr);
	}

	private void getDBObjectDescription(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// 1. Check description existance
		if(!chkDBPathDescription(inMessage))return;

		String ksName = inMessage.getData().get(_ksname_link);
		String cfName = inMessage.getData().get(_cfname_link);
		String cName = inMessage.getData().get(_cname_link);
		
		// 3. create html context		
		inMessage.setHtmlContent(cdb.getAccessDescription(
				ksName,
				cfName,
				cName));		
	}
	
	private boolean chkDBPathDescription(IMessage inMessage){
		String ksName = inMessage.getData().get(_ksname_link);
		String cfName = inMessage.getData().get(_cfname_link);
		String cName = inMessage.getData().get(_cname_link);
		
		// 1. Check description existance
		if(!cdb.checkDescriptions(ksName, cfName, cName)){
			inMessage.setError(trace + String.format("Invalid description: %s.%s['%s']['%s']['%s']",
					ksName,
					cfName,
					"scfeyID",
					"columnTYPE",
					cName));
			return false;
		}		
		
		return true;
	}
}
