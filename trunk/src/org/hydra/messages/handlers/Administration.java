package org.hydra.messages.handlers;

import java.util.Map;
import org.hydra.beans.Applications;
import org.hydra.beans.StatisticsCollector;
import org.hydra.beans.db.ColumnBean;
import org.hydra.beans.db.ColumnFamilyBean;
import org.hydra.beans.db.KeyspaceBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

public class Administration extends AMessageHandler { // NO_UCD
	
	public IMessage describeHydra(IMessage inMessage) {
		Result result = BeansUtils.getWebSessionBean(Constants._beans_statistics_collector);
		
		if(result.isOk() && result.getObject() instanceof StatisticsCollector){
			StatisticsCollector statisticsCollector = (StatisticsCollector) result.getObject();
			inMessage.setHtmlContent(statisticsCollector.getHtmlReport(inMessage));
			return inMessage;
		}
		getLog().error("Could not find statistics bean object!");
		inMessage.setError("Could not find statistics bean object!");
		
		return inMessage;
	}
	
	public IMessage describeReports(IMessage inMessage){
		String destination = "_describeReports";
		String result = Utils.makeJSLink("Hydra",
				String.format("handler:'%s'", this.getClass().getSimpleName()),
				String.format("action:'%s'", "describeHydra"),
				String.format("dest:'%s'", destination)
				);
		result += " &bull; " + Utils.makeJSLink(MessagesManager.getText("text.Applications", 
					null, 
					inMessage.getData().get(IMessage._data_locale)), 
				String.format("handler:'%s'", this.getClass().getSimpleName()),
				String.format("action:'%s'", "describeApplications"),
				String.format("dest:'%s'", destination)
			);
		result += " &bull; " + Utils.makeJSLink(MessagesManager.getText("text.Server", 
				null, 
				inMessage.getData().get(IMessage._data_locale)), 
			String.format("handler:'%s'", this.getClass().getSimpleName()),
			String.format("action:'%s'", "describeServer"),
			String.format("dest:'%s'", destination)
		);
		result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"),
				destination);
		inMessage.setHtmlContent(result);
		
		return inMessage;
	}
	
	public IMessage describeApplications(IMessage inMessage){
		Applications apps = (Applications) BeansUtils.getBean(Constants._beans_hydra_applications);
		inMessage.setHtmlContent(apps.getDescription());
		return inMessage;
	}
	
	public IMessage describeServer(IMessage inMessage){
		inMessage.setServerInfo(IMessage._temp_value);
		String result = String.format(MessagesManager.getTemplate("template.table.with.class"),
				"table.name.value",
				String.format("<tr><th colspan='2'>%s</th>", "Server")
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", "Type", inMessage.getData().get(IMessage._temp_value))
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", "Protocol", inMessage.getData().get(IMessage._url_scheme))
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", "Name", inMessage.getData().get(IMessage._url_server_name))
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", "Port", inMessage.getData().get(IMessage._url_server_port))
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", "Applicication ID", inMessage.getData().get(IMessage._app_id))
				);

		inMessage.setHtmlContent(result + "</pre>");
		return inMessage;
	}
	
	public IMessage describeCassandra(IMessage inMessage){
		CassandraAccessorBean cassandraAccessorBean = BeansUtils.getAccessor();
		if(!cassandraAccessorBean.isValid()){
			inMessage.setError("Could not access to cassandra!");
			return inMessage;
		}
		
		String result = "";
		String format = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		result += String.format(format,"Cluster name", cassandraAccessorBean.getClusterName());
		result += String.format(format,"Ip", cassandraAccessorBean.getHost());
		result += String.format(format,"Port", cassandraAccessorBean.getPort());
		result += String.format(format,"Version", cassandraAccessorBean.getProtocolVersion());
		result += String.format(format,"Keyspaces", getKspJSLinks(cassandraAccessorBean));
		result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), "_ksp_desc_div");
		
		inMessage.setHtmlContent(result);
		
		return inMessage;
	}
	
	private String getKspJSLinks(CassandraAccessorBean inAccessor) {
		StringBuffer result = new StringBuffer();	
		int counter = 0;
		
		for(String keyspaceName: inAccessor.getServerKeyspaces()){
//			if(inAccessor.getDescriptor().containsKeyspace(keyspaceName)){
//				if(counter++ != 0) result.append(", ");
//				result.append(Utils.makeJSLink(inAccessor.getDescriptor().getKeyspace(keyspaceName).getName(), 
//						String.format("handler:'%s'", this.getClass().getSimpleName()),
//						String.format("dest:'%s'", "_ksp_desc_div"),
//						String.format("action:'%s'","describeKeyspace"),
//						String.format("cs_ksp:'%s'", keyspaceName)));
//				
//			}else
//				getLog().warn("Could not find description for keyspace: " + keyspaceName);
//TODO Fix it later			
		}
		
		return result.toString();
	}		
			
	public IMessage describeKeyspace(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Utils.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
//		KeyspaceBean ksp = BeansUtils.getDescriptor().getKeyspace(inMessage.getData().get(IMessage._data_cs_ksp));
		KeyspaceBean ksp = new KeyspaceBean();
//TODO Fix it later		
		if(ksp != null){
			String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
			String result = String.format(formatStrong, "Keyspace", ksp.getName());			
			String cfLinks = "";
			for(String cfName: ksp.getColFamilies().keySet()){			
				if(cfLinks.length() > 0)
					cfLinks += ", ";
				cfLinks += Utils.makeJSLink(cfName, 
						String.format("handler:'%s'", this.getClass().getSimpleName()),
						String.format("dest:'%s'", "_admin_cf_div"),
						String.format("action:'%s'", "getCassabdraColumnsDescription"),
						String.format("cs_ksp:'%s'", ksp.getName()),
						String.format("cs_cf:'%s'", cfName));
			}			
			// Append all column family links
			result += String.format(formatStrong, "Column families", cfLinks.toString());
			if(cfLinks.length() > 0)
				result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), 
						"_admin_cf_div");
			inMessage.setHtmlContent(result);
			return inMessage;
		}
		inMessage.setError("Could not found keyspace: " + inMessage.getData().get(IMessage._data_cs_ksp));

		return inMessage;
	}	

	public IMessage getCassabdraColumnsDescription(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Utils.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
//		KeyspaceBean kspBean = BeansUtils.getDescriptor().getKeyspace(inMessage.getData().get(IMessage._data_cs_ksp));
		KeyspaceBean kspBean = new KeyspaceBean();
		//TODO Fix it later				
		if(kspBean == null){
			inMessage.setError(trace + "Could not find Ksp: " + inMessage.getData().get(IMessage._data_cs_ksp));
			return inMessage;
		}
		
		ColumnFamilyBean cfBean = kspBean.getColumnFamilyByName(inMessage.getData().get(IMessage._data_cs_cf));
		if(cfBean == null){
			inMessage.setError(trace + "Could not find Cf: " + inMessage.getData().get(IMessage._data_cs_cf));
			return inMessage;	
		}		
		

		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		int counter = 0;
		String resultLinks = "";
		
		String result = String.format(formatStrong, "Column family", cfBean.getName()
				+ "&nbsp;" 
				+ Utils.wrap2HTMLTag("sup", DBUtils.getJSLinkShowAllColumns(kspBean, cfBean)));
		
		
		for(Map.Entry<String, ColumnBean> entryCFKey: cfBean.columns.entrySet()){
			if(counter++ != 0)
				resultLinks += ", ";
			resultLinks += Utils.makeJSLink(entryCFKey.getKey(), 
					String.format("handler:'%s'", this.getClass().getSimpleName()),
					String.format("dest:'%s'", "_admin_col_div"),
					String.format("action:'%s'", "describeColumn"),
					String.format("cs_ksp:'%s'", kspBean.getName()),
					String.format("cs_cf:'%s'", cfBean.getName()),
					String.format("cs_col:'%s'", entryCFKey.getKey())
				);									
		}		
		result += String.format(formatStrong, "Columns", resultLinks);
		
		// Append tail div for child elements
		if(counter > 0)
			result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), "_admin_col_div");

		inMessage.setHtmlContent(result);
		return inMessage;
	}		
	
	public IMessage describeColumn(IMessage inMessage){
		String keyspaceName = inMessage.getData().get(IMessage._data_cs_ksp);
		String columnFamilyName = inMessage.getData().get(IMessage._data_cs_cf); 
		String columnName = inMessage.getData().get(IMessage._data_cs_col); 
		
		String pathStr = String.format("%s--->%s", keyspaceName, columnFamilyName);
		CassandraVirtualPath path = new CassandraVirtualPath(DBUtils.getDescriptor(), pathStr);
		
		if(!path.isValid()){
			inMessage.setError("Path to DB objects is valid: " + pathStr);
			return inMessage;
		}
		
		Result result = path._cfBean.getColumn(columnName);
		if(!result.isOk() || !(result.getObject() instanceof ColumnBean)){
				inMessage.setError("Could not find cassandra column description!");
				return inMessage;
		}
		
		ColumnBean column = (ColumnBean)result.getObject();
		
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		String inputBoxID = keyspaceName + columnFamilyName;
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", "%s.%s['%s']['%s']"), 
				keyspaceName,
				columnFamilyName,
				String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
					Utils.makeJSLink(column.getName(), 
							String.format("handler:'%s'", this.getClass().getSimpleName()),
							String.format("dest:'%s'", resultDivID),
							String.format("action:'%s'", "cs_select_super_column"),
							String.format("cs_ksp:'%s'", keyspaceName),
							String.format("cs_cf:'%s'", columnFamilyName),
							String.format("cs_key:$('%s').value", inputBoxID))
				)
				+
				String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID)
		);		
		return inMessage;
	}	

}
