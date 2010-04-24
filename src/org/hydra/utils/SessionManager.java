package org.hydra.utils;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.springframework.jms.IllegalStateException;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public final class SessionManager {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionManager");
	
	public static WebContext getWebContext(){
		return WebContextFactory.get();
	}
	
	public static HttpSession getHttpSession(){
		return getWebContext().getSession();
	}
	
	public static Object getFromSession(String inKey){
		Object result = getHttpSession().getAttribute(inKey);
		if(result != null)
			return getHttpSession().getAttribute(inKey);
		return null;
	}
	
	public static void setToSession(String inKey, Object inObject){
		getHttpSession().setAttribute(inKey, inObject);
	}
	
	public static Object getSessionValue(String inKey){
		return getFromSession(inKey);
	}
	
	public static String setSessionValue(String inKey, Object obj){
		setToSession(inKey, obj);
		return "OK";
	}

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
	
	
	public static Result getHttpSessionValue(HttpSession session,
			String inKey) {
		Result result = new Result();
		try{
			result.setObject(session.getAttribute(inKey));
			result.setResult(true);
		}catch (IllegalStateException e) {
			 _log.error(e.getMessage());
			result.setResult(false);
			result.setResult(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Attache session data (locale, userId and etc.)
	 * @param inMessage
	 * @param inSession 
	 */
	public static void attachSessionData(MessageBean inMessage, HttpSession inSession) {
		// Validate message's data
		if(inMessage.getData() == null) inMessage.setData(new HashMap<String, String>());
		
		// Session locale
		Result result = getHttpSessionValue(inSession, IMessage._string_locale);
		if(result.isOk()){
			inMessage.getData().put(IMessage._string_locale, (String)result.getObject());
		}else inMessage.getData().put(IMessage._string_locale, null);
		
		// Session userId
		result = getHttpSessionValue(inSession, IMessage._string_userId);
		if(result.isOk()){
			inMessage.getData().put(IMessage._string_userId, (String)result.getObject());
		}else inMessage.getData().put(IMessage._string_userId, null);
	}
	
	/**
	 * 
	 * Detache session data (locale, userId and etc.)
	 * @param inMessage
	 */
	public static void detachSessionData(MessageBean inMessage) {
		inMessage.getData().remove(IMessage._string_locale);
		inMessage.getData().remove(IMessage._string_userId);
		inMessage.getData().remove(IMessage._data_sessionId);
		inMessage.getData().remove(IMessage._data_handler);
	}

	public static boolean isDebug() {
		return AppContext.getApplicationContext().containsBean(Constants._debug_mode);
	}
		
}
