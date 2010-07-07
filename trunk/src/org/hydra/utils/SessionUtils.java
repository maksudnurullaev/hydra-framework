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
