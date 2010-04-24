package org.hydra.messages.handlers;

import org.hydra.collectors.StatisticsCollector;
import org.hydra.db.beans.KSName;
import org.hydra.db.server.CassandraBean;
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
	public static final String _defaultContentBodyIDTail = "admin.content.body.tail";
	
	public static final String _what_hydra_desc = "hydra_desc";
	public static final String _what_hydra_bean_desc = "hydra_bean_desc";
	public static final String _what_cassandra_desc = "cassandra_desc";
	public static final String _what_cassandra_ksname_desc = "cassandra_ksname_desc";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage)) return inMessage;
		
		// - Handle request message by type == global
		if(inMessage.getData().get(IMessage._data_what).equals(_what_hydra_desc)){			
			inMessage.setHtmlContent(getDescriptionHtmlHydraBeans());
		}else if(inMessage.getData().get(IMessage._data_what).equals(_what_cassandra_desc)){
			inMessage.setHtmlContent(getDescriptionHTMLCassandra()
					+ String.format("<hr /><div id='%s'>...</div>", _defaultContentBodyIDTail));
		}else if(inMessage.getData().get(IMessage._data_what).equals(_what_cassandra_ksname_desc)){
			inMessage.setHtmlContent(getCassandraKSNameDesc(inMessage.getData().get(IMessage._data_kind)));
		}else{
			String errorMsg = trace + String.format("error.unknown.message.type.(What/Kind): (%s/%s)"
				, inMessage.getData().get(IMessage._data_what)
				, inMessage.getData().get(IMessage._data_kind));
			getLog().error(errorMsg);			
			inMessage.setError(errorMsg);
		}
		return inMessage;
		
	}
	
	private String getCassandraKSNameDesc(String  inKSNameBean) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		Result result = SessionManager.getBean(inKSNameBean);
		
		if(result.isOk() && result.getObject() instanceof KSName){
			KSName desriptor = (KSName) result.getObject();
			return desriptor.getTablesDescriptionHTML();
		}
		return trace + MessagesManager.getTextManager().getTextByKey("error.bean.statistics.not.found");		
	}

	private String getDescriptionHTMLCassandra(){
		Result result = SessionManager.getBean(Constants._beans_cassandra_server);
		
		if(result.isOk() && result.getObject() instanceof CassandraBean){
			CassandraBean server = (CassandraBean) result.getObject();
			return server.getHTMLReport();
		}
		return MessagesManager.getTextManager().getTextByKey("error.bean.statistics.not.found");		
	}
	
	private String getDescriptionHtmlHydraBeans() {
		Result result = SessionManager.getBean(Constants._beans_statistics_collector);
		
		if(result.isOk() && result.getObject() instanceof StatisticsCollector){
			StatisticsCollector statisticsCollector = (StatisticsCollector) result.getObject();
			return statisticsCollector.getHtmlReport();
		}
		return MessagesManager.getTextManager().getTextByKey("error.bean.statistics.not.found");		
	}

}
