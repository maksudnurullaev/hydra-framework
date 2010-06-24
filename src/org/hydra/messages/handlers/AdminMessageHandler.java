package org.hydra.messages.handlers;

import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.hydra.collectors.StatisticsCollector;
import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;
import org.hydra.utils.SessionManager;

public class AdminMessageHandler extends AMessageHandler {
	public static final String _handler_name = "AdminMessage";
	public static final String _defaultContentBodyID = "admin.content.body";
	
	public static final String _action_describe_hydra       = "describe_hydra";
	public static final String _action_describe_hydra_bean  = "describe_hydra";
	
	public static final String _action_describe_cassandra     = "describe_cassandra";
	public static final String _action_describe_cassandra_ksp = "describe_cassandra_ksp";
	public static final String _action_describe_cassandra_cf  = "describe_cassandra_cf";
	// Cassandra actions
	public static final String _action_cs_describe_column       = "cs_describe_column"; 
	public static final String _action_cs_select_column         = "cs_select_column";     
	public static final String _action_cs_select_super_column   = "cs_select_super_column";     
	public static final String _action_cs_insert                = "cs_insert";
	public static final String _action_cs_delete                = "cs_delete";
	
	public static final String _admin_cf_divId  = "_admin_cf_div";
	public static final String _admin_col_divId = "_admin_col_div";
	
	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		if(!testParameters(inMessage, IMessage._data_action)) return inMessage;
		
		// - Handle request message by type == global
		if(inMessage.getData().get(IMessage._data_action).equals(_action_describe_hydra)){			
			describeHydra(inMessage);
		}else if(inMessage.getData().get(IMessage._data_action).equals(_action_describe_cassandra)){
			describeCassandra(inMessage);
		}else if(inMessage.getData().get(IMessage._data_action).equals(_action_describe_cassandra_ksp)){
			if(!testParameters(inMessage, IMessage._data_cs_ksp)) return inMessage;
			describeKsp(inMessage);
		}else if(inMessage.getData().get(IMessage._data_action).equals(_action_describe_cassandra_cf)){
			if(!testParameters(inMessage, IMessage._data_cs_ksp, IMessage._data_cs_cf)) return inMessage;
			describeCf(inMessage);
		}else if(inMessage.getData().get(IMessage._data_action).equals(_action_cs_describe_column)){
				if(!testParameters(inMessage,IMessage._data_cs_ksp, IMessage._data_cs_cf, IMessage._data_cs_col)){
					String errorStr = "Missing parameters _cs_key or _cs_col!";
					getLog().error(errorStr);
					inMessage.setError(errorStr);
					return inMessage;
				}	
				DBUtils.describeColumn(SessionManager.getCassandraDescriptor(), inMessage);		
		}else if(inMessage.getData().get(IMessage._data_action).equals(_action_cs_select_column)){
				if(!testParameters(inMessage, IMessage._data_cs_ksp, IMessage._data_cs_cf, IMessage._data_cs_key, IMessage._data_cs_col)){
					String errorStr = "Missing parameters _cs_key or _cs_col!";
					getLog().error(errorStr);
					inMessage.setError(errorStr);
					return inMessage;
				}			
				selectColumn(inMessage);			
		}else{
			String errorMsg = trace + String.format("Unknown action: %s\n", 
					inMessage.getData().get(IMessage._data_action));
						
			inMessage.setError(errorMsg);
			getLog().error(errorMsg);
		}
		return inMessage;
	}
	
	private void selectColumn(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		//CassandraVirtualPath path = new CassandraVirtualPath(inMessage);
		
		List<Column> columns = null;
		/* 
		 * SessionManager.getCassandraAccessor().getDBColumns(
				path.ksp,
				path.cf,
				path.key,
				path.col);
		 */

		if(columns == null){
			inMessage.setError("No column found!");
			return;
		}
		
		getLog().debug("Got column count: " + columns.size());		
		
		String result = "";
		int count = 0;
		for (Column column:columns){
			if(count++ > 0)
				result += "<hr />";
			result += getColumnDescription(column);
		}
		
		inMessage.setHtmlContent(result);
	}
	
	private String getColumnDescription(Column inColumn){
		String format = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		String result = "";
		result += String.format(format, "_name_", DBUtils.bytes2UTF8String(inColumn.name, 32));				
		result += String.format(format, "&nbsp;_timestamp_", inColumn.timestamp);
		result += String.format(format, "&nbsp;_value_", DBUtils.bytes2UTF8String(inColumn.value, 32));
		return result;
	}	
	
	private void describeCf(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		KeyspaceBean kspBean = SessionManager.getCassandraDescriptor().getKeyspace(inMessage.getData().get(IMessage._data_cs_ksp));
		
		if(kspBean == null){
			inMessage.setError(trace + "Could not find Ksp: " + inMessage.getData().get(IMessage._data_cs_ksp));
			return;
		}
		
		ColumnFamilyBean cfBean = kspBean.getColumnFamilyByName(inMessage.getData().get(IMessage._data_cs_cf));
		if(cfBean == null){
			inMessage.setError(trace + "Could not find Cf: " + inMessage.getData().get(IMessage._data_cs_cf));
			return;	
		}		
		

		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		int counter = 0;
		String resultLinks = "";
		
		String result = String.format(formatStrong, "Column family", cfBean.getName());
		
		for(Map.Entry<String, ColumnBean> entryCFKey: cfBean.columns.entrySet()){
			if(counter++ != 0)
				resultLinks += ", ";
			resultLinks += Constants.makeJSLink(entryCFKey.getKey(),
					"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s'",
					AdminMessageHandler._handler_name,
					AdminMessageHandler._admin_col_divId,
					IMessage._data_action, AdminMessageHandler._action_cs_describe_column,
					IMessage._data_cs_ksp, kspBean.getName(),
					IMessage._data_cs_cf, cfBean.getName(),
					IMessage._data_cs_col, entryCFKey.getKey()
				);
						
		}		
		result += String.format(formatStrong, "Columns", resultLinks);
		
		// Append tail div for child elements
		if(counter > 0)
			result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), _admin_col_divId);

		inMessage.setHtmlContent(result);
	}	
	
	private void describeKsp(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		KeyspaceBean ksp = SessionManager.getCassandraDescriptor().getKeyspace(inMessage.getData().get(IMessage._data_cs_ksp));
		
		if(ksp != null){
			inMessage.setHtmlContent(ksp.getCfHTMLDescription());
			return;
		}
		inMessage.setError("Could not found keyspace: " + inMessage.getData().get(IMessage._data_cs_ksp));		
	}

	private void describeCassandra(IMessage inMessage){
		CassandraAccessorBean cassandraAccessorBean =SessionManager.getCassandraAccessor();
		
		String result = "";
		String format = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		result += String.format(format,"Cluster name", cassandraAccessorBean.getClusterName());
		result += String.format(format,"Ip", cassandraAccessorBean.getHost());
		result += String.format(format,"Port", cassandraAccessorBean.getPort());
		result += String.format(format,"Version", cassandraAccessorBean.getProtocolVersion());
		result += String.format(format,"Keyspaces", getKspJSLinks(cassandraAccessorBean));
		result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), KeyspaceBean._ksp_desc_divId);
		
		inMessage.setHtmlContent(result);
	}
	
	private void describeHydra(IMessage inMessage) {
		Result result = SessionManager.getBean(Constants._beans_statistics_collector);
		
		if(result.isOk() && result.getObject() instanceof StatisticsCollector){
			StatisticsCollector statisticsCollector = (StatisticsCollector) result.getObject();
			inMessage.setHtmlContent(statisticsCollector.getHtmlReport());
			return;
		}
		getLog().error("Could not find statistics bean object!");
		inMessage.setError("Could not find statistics bean object!");
	}

	public String getHTMLReport(CassandraAccessorBean inAccessor) {		
		StringBuffer result = new StringBuffer();
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		try{
			result.append(String.format(formatStrong,"Cluster name", inAccessor.getClusterName()));
			result.append(String.format(formatStrong,"Ip", inAccessor.getHost()));
			result.append(String.format(formatStrong,"Port", inAccessor.getPort()));
			result.append(String.format(formatStrong,"Version", inAccessor.getProtocolVersion()));
			result.append(String.format(formatStrong,"Keyspaces", getKspJSLinks(inAccessor)));
			result.append(String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), KeyspaceBean._ksp_desc_divId));
		}catch (Exception e) {
			result.append(e.getMessage());
			getLog().error(e.getMessage());
		}			
		return result.toString();
	}	
	
	private String getKspJSLinks(CassandraAccessorBean inAccessor) {
		StringBuffer result = new StringBuffer();	
		int counter = 0;
		
		for(String keyspaceName: inAccessor.getServerKeyspaces()){
			if(inAccessor.getDescriptor().containsKeyspace(keyspaceName)){
				if(counter++ != 0){result.append(", ");}
				result.append(Constants.makeJSLink(inAccessor.getDescriptor().getKeyspace(keyspaceName).getName(), 
						"handler:'%s',dest:'%s',%s:'%s',%s:'%s'", 
						_handler_name,
						KeyspaceBean._ksp_desc_divId,
						IMessage._data_action, _action_describe_cassandra_ksp,
						IMessage._data_cs_ksp, inAccessor.getDescriptor().getKeyspace(keyspaceName).getName()
						));
				
			}else
				getLog().warn("Could not find description for keyspace: " + keyspaceName);
		}
		
		return result.toString();
	}		
	
}
