package org.hydra.utils;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;

public final class SessionUtils {
	static Log _log = LogFactory.getLog("org.hydra.utils.SessionManager");
	
	/**
	 * Attache session data (locale, userId and etc.)
	 * @param inMessage
	 * @param inSession 
	 */
	public static void attachIMessageSessionData(MessageBean inMessage, WebContext inWebContext) {
		_log.debug("inWebContext.getSession() is not null: " + (inWebContext.getSession() != null));
		_log.debug("Attach new data to session with id: " + inWebContext.getSession().getId());
		
		String tempString = null;
		tempString = inWebContext.getSession().getId();
		_log.debug("Set session Id: " + tempString);
		inMessage.getData().put(IMessage._data_sessionId, tempString);
		
		tempString = getLocale(inWebContext.getSession());
		inMessage.getData().put(IMessage._data_locale, tempString);
		_log.debug("Set session locale: " + tempString);
		
		tempString = getUserId(inWebContext.getSession());
		inMessage.getData().put(IMessage._data_userId, tempString);
		_log.debug("Set userId: " + tempString);
		
		inMessage.setHttpSession(inWebContext.getSession());
	}
	
	public static String getUserId(HttpSession session) {
		return (String) session.getAttribute(IMessage._data_userId);
	}

	public static void setUserId(HttpSession session, String userId) {
		session.setAttribute(IMessage._data_userId, userId);
	}	
	
	public static String getLocale(HttpSession session) {
		_log.debug("Try to get 'locale' defenition from web session!");
		_log.debug("Web session is not null: " + (session != null));
		Object testObject = session.getAttribute(IMessage._data_locale);
		if(testObject != null){
			_log.debug("Locale from incoming message structure : " + (String)testObject);
			return (String) testObject;
		}else{
			_log.warn("Could not find 'locale' definition from web session object!");
		}
		_log.debug("Get default locale: " + MessagesManager.getTextManager().getDefaultLocale());
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
		//TODO [later] ... we remove it later if it's necessary!!!
//		if(inMessage != null && inMessage.getData() != null && !AppContext.isDebugMode()){
//			inMessage.getData().remove(IMessage._data_handler);
//			inMessage.getData().remove(IMessage._data_sessionId);
//			inMessage.getData().remove(IMessage._data_locale);
//			inMessage.getData().remove(IMessage._data_userId);			
//			inMessage.getData().remove(IMessage._data_key);			
//		}
	}

	public static boolean isDebug() {
		return AppContext.getApplicationContext().containsBean(Constants._debug_mode);
	}		
}
