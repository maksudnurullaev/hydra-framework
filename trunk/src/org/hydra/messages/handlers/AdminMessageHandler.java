package org.hydra.messages.handlers;

import java.util.Map;

import org.hydra.collectors.StatisticsCollector;
import org.hydra.db.beans.Ksp;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;
import org.hydra.utils.SessionManager;

public class AdminMessageHandler extends AMessageHandler {
	public static final String _handler_name = "AdminMessage";
	public static final String _defaultContentBodyID = "admin.content.body";
	
	public static final String _what = "what";	
	public static final String _what_hydra_desc = "hydra_desc";
	public static final String _what_hydra_bean_desc = "hydra_bean_desc";
	public static final String _what_cassandra_desc = "cassandra_desc";
	public static final String _what_cassandra_ksp_desc = "cassandra_ksp_desc";
	
	public static final String _kind = "kind";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage, _what, _kind)) return inMessage;
		
		// - Handle request message by type == global
		if(inMessage.getData().get(IMessage._data_what).equals(_what_hydra_desc)){			
			inMessage.setHtmlContent(getDescriptionHtmlHydraBeans(inMessage));
		}else if(inMessage.getData().get(IMessage._data_what).equals(_what_cassandra_desc)){
			inMessage.setHtmlContent(getDescriptionHTMLCassandra(inMessage));
		}else if(inMessage.getData().get(IMessage._data_what).equals(_what_cassandra_ksp_desc)){
			inMessage.setHtmlContent(getCassandraKSNameDesc(inMessage.getData().get(IMessage._data_kind)));
		}else {
			String errorMsg = trace + String.format("error.unknown.message.type.(What/Kind):(%s/%s)\n", 
					inMessage.getData().get(IMessage._data_what),
					inMessage.getData().get(IMessage._data_kind));
			
			if(AppContext.isDebugMode()){
				for(Map.Entry<String, String> dataEntry:inMessage.getData().entrySet())
					errorMsg += String.format("%s/%s\n", dataEntry.getKey(), dataEntry.getValue());
			}
			
			inMessage.setError(errorMsg);
			getLog().error(errorMsg);
		}
		return inMessage;
	}
	
	private String getCassandraKSNameDesc(String  inKSName) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		ACassandraDescriptorBean csd = SessionManager.getCassandraServerDescriptor();
		
		if(csd != null){
			Ksp ksname = csd.getKSName(inKSName);
			return ksname.getCFNamesDescriptionHTML();
		}
		
		return String.format("%s %s: %s", 
				trace, 
				"error.bean.not.found", 
				inKSName);		
	}

	private String getDescriptionHTMLCassandra(IMessage inMessage){
		Result result = SessionManager.getBean(Constants._beans_cassandra_accessor);
		
		if(result.isOk() && result.getObject() instanceof CassandraAccessorBean){
			return ((CassandraAccessorBean) result.getObject()).getHTMLReport();
		}
		return MessagesManager.getTextManager().getTextByKey("error.bean.statistics.not.found",
				null, 
				inMessage.getData().get(IMessage._data_locale));		
	}
	
	private String getDescriptionHtmlHydraBeans(IMessage inMessage) {
		Result result = SessionManager.getBean(Constants._beans_statistics_collector);
		
		if(result.isOk() && result.getObject() instanceof StatisticsCollector){
			StatisticsCollector statisticsCollector = (StatisticsCollector) result.getObject();
			return statisticsCollector.getHtmlReport();
		}
		return MessagesManager.getTextManager().getTextByKey("error.bean.statistics.not.found",
				null,
				inMessage.getData().get(IMessage._data_locale));
	}

}
