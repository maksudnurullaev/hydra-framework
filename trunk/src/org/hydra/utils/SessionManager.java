package org.hydra.utils;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public final class SessionManager {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionManager");
	
	public static WebApplicationContext getWebApplicationContext() {
		return ContextLoader.getCurrentWebApplicationContext();
	}
	
	public static Result getBean(String inBeanId){
		Result result = new Result();
		try{
			result.setObject(getWebApplicationContext().getBean(inBeanId));
			result.setResult(true);
		}catch(Exception e){
			result.setResult(e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Attache session data (locale, userId and etc.)
	 * @param inMessage
	 * @param inSession 
	 */
	public static void attachIMessageSessionData(MessageBean inMessage, WebContext inWebContext) {
		_log.debug("Attach new data to session with id: " 
				+ inWebContext.getSession().getId());
		inMessage.getData().put(IMessage._data_sessionId,inWebContext.getSession().getId());
		inMessage.getData().put(IMessage._data_locale, getLocale(inWebContext.getSession()));
		inMessage.getData().put(IMessage._data_userId, getUserId(inWebContext.getSession()));
		
		inMessage.setHttpSession(inWebContext.getSession());
	}
	
	public static String getUserId(HttpSession session) {
		return (String) session.getAttribute(IMessage._data_userId);
	}

	public static void setUserId(HttpSession session, String userId) {
		session.setAttribute(IMessage._data_userId, userId);
	}	
	
	public static String getLocale(HttpSession session) {
		if(session.getAttribute(IMessage._data_locale) != null)
			return (String) session.getAttribute(IMessage._data_locale);
		return MessagesManager.getTextManager().getDefaultLocale();
	}

	public static void setLocale(HttpSession session, String locale) {
		session.setAttribute(IMessage._data_locale,locale);
	}	
	
	/**
	 * 
	 * Detache session data (locale, userId and etc.)
	 * @param inMessage
	 * @return 
	 */
	public static void detachIMessageSessionData(IMessage inMessage) {
		if(inMessage != null && inMessage.getData() != null && !AppContext.isDebugMode()){
			inMessage.getData().remove(IMessage._data_handler);
			inMessage.getData().remove(IMessage._data_sessionId);
			inMessage.getData().remove(IMessage._data_locale);
			inMessage.getData().remove(IMessage._data_userId);			
			inMessage.getData().remove(IMessage._data_kind);			
		}
	}

	public static boolean isDebug() {
		return AppContext.getApplicationContext().containsBean(Constants._debug_mode);
	}
	
	public static CassandraDescriptorBean getCassandraServerDescriptor() {
		Result result = getBean(Constants._beans_cassandra_descriptor);
		
		if(result.isOk() && result.getObject() instanceof CassandraDescriptorBean){
			_log.debug("Found bean: " + Constants._beans_cassandra_descriptor);
			return (CassandraDescriptorBean) result.getObject();
		}
		_log.fatal("Could not find bean: " + Constants._beans_cassandra_descriptor);
		return null;
	}	
	
	public static CassandraAccessorBean getCassandraServerAccessor() {
		Result result = getBean(Constants._beans_cassandra_accessor);
		
		if(result.isOk() && result.getObject() instanceof CassandraAccessorBean){
			_log.debug("Found bean: " + Constants._beans_cassandra_accessor);
			return (CassandraAccessorBean) result.getObject();
		}
		_log.fatal("Could not find bean: " + Constants._beans_cassandra_accessor);
		return null;
	}		
}
