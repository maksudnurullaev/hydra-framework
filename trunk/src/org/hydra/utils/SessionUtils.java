package org.hydra.utils;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionManager");
	
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
		inMessage.getData().put(IMessage._user_id, tempString);
		_log.debug("Set userId: " + tempString);
		
		inMessage.setHttpSession(inWebContext.getSession());
	}
	
	private static String getUserId(HttpSession session) {
		return (String) session.getAttribute(IMessage._user_id);
	}

	private static String getLocale(HttpSession session) {
		_log.debug("Try to get 'locale' defenition from web session!");
		_log.debug("Web session is not null: " + (session != null));
		String result = (String)session.getAttribute(IMessage._data_locale);
		if(result != null){
			_log.debug("Get 'locale' definition from current session: " + result);
			return result;
		}
		_log.debug("Could not find 'locale' definition from web session object!");
		_log.debug("Get default locale: " + MessagesManager.getTextManager().getDefaultLocale());
		return MessagesManager.getTextManager().getDefaultLocale();
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
	
}
