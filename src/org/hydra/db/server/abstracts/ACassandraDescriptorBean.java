package org.hydra.db.server.abstracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.SessionManager;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraDescriptorBean extends ALogger {

	private Map<String, KeyspaceBean> _keyspaces = new HashMap<String, KeyspaceBean>();
	
	public static final String PATH2COLUMN5 = "%s.%s['%s']['%s']['%s']";
	public static final String PATH2COLUMN4   = "%s.%s['%s']['%s']";	
	
	public boolean containsKeyspace(String keyspaceName) {
		return _keyspaces.containsKey(keyspaceName);
	}	
	
	public ACassandraDescriptorBean() {
		super();
	}

	public void setKeyspaces(Set<KeyspaceBean> inKeyspaces) {
		_keyspaces.clear();
		for(KeyspaceBean entryKsp: inKeyspaces)
			_keyspaces.put(entryKsp.getName(), entryKsp);
	}

	public KeyspaceBean getKeyspace(String inKeyspaceName) {
		if(_keyspaces.containsKey(inKeyspaceName)){
			getLog().debug("Get keyspace: " + inKeyspaceName);
			return _keyspaces.get(inKeyspaceName);
		}
		getLog().warn("Could not find keyspace: " + inKeyspaceName);
		return null;
	}

	public void describeColumn(IMessage inMessage){
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		
		String keyspaceName = inMessage.getData().get(IMessage._data_cs_ksp);
		String columnFamilyName = inMessage.getData().get(IMessage._data_cs_cf); 
		String columnName = inMessage.getData().get(IMessage._data_cs_col); 
		
		// - Test incoming message for access path		
		ColumnBean column = SessionManager.getCassandraDescriptor().getColumnBean(
				keyspaceName,
				columnFamilyName,
				columnName);
		
		getLog().debug(String.format("Get column(%s) with type: %s", 
				column.getName(),
				column.getSuper()));
		
		if(column == null){
			inMessage.setError("Could not find cassandra column description!");
			return;
		}
		
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		String inputBoxID = keyspaceName + columnFamilyName;
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		if(column.getSuper().equals(ColumnBean.SUPER_COLUMN)){
			getLog().debug("Create html path for column!");
			inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", PATH2COLUMN5), 
					keyspaceName,
					columnFamilyName,
					String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
						Constants.makeJSLink(column.getSuper(), 
								"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value",
								//         1         2   3   4   5   6   7   8   9    10  
								AdminMessageHandler._handler_name, // 1
								resultDivID, // 2
								IMessage._data_action, AdminMessageHandler._action_cs_select_super_column, // 3,4   
								IMessage._data_cs_ksp, keyspaceName,     // 5,6
								IMessage._data_cs_cf, columnFamilyName,     // 7,8
								IMessage._data_cs_key, inputBoxID  // 9,10
								),
					Constants.makeJSLink(column.getName(), 
							"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value",
							//         1         2   3   4   5   6   7   8   9  10  11    12  
							AdminMessageHandler._handler_name, // 1
							resultDivID, // 2
							IMessage._data_action, AdminMessageHandler._action_cs_select_column, // 3,4   
							IMessage._data_cs_ksp, keyspaceName,     // 5,6
							IMessage._data_cs_cf, columnFamilyName,     // 7,8
							IMessage._data_cs_col, column.getName(),      // 9,10
							IMessage._data_cs_key, inputBoxID  // 11,12
							)
					)
					+
					String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID)
			);
		}else if(column.getSuper().equals(ColumnBean.SUPER_LINK)){		
			getLog().debug("Create html path for link!");
			inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", PATH2COLUMN4), 
					keyspaceName,
					getKeyspace(keyspaceName).getLinkTableName(),
					String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
					Constants.makeJSLink(column.getName(), 
							"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value", 
							//         1         2   3   4   5   6   7   8   9  10  11    12  
							AdminMessageHandler._handler_name, // 1
							resultDivID, // 2
							IMessage._data_action, AdminMessageHandler._action_cs_select_column, // 3,4  
							IMessage._data_cs_ksp, keyspaceName,     // 5,6
							IMessage._data_cs_cf, columnFamilyName,     // 7,8
							IMessage._data_cs_col, column.getName(),       // 9,10
							IMessage._data_cs_key, inputBoxID) // 11,12
					)
					+
					String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID)
				);
		}else{ // unknown super type
			inMessage.setError(String.format("Uknown super type(%s) for column: %s",
					column.getSuper(),
					column.getName()));
		}
	}
	




}