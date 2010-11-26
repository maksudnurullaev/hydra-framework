package org.hydra.utils;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");
	
	/**
	 * Attache session data (locale, userId and etc.)
	 * @param inMessage
	 * @param inSession 
	 */
	public static void attachIMessageSessionData(MessageBean inMessage, WebContext inWebContext) {
		// 1. set web application id
		WebApplications webApplications = (WebApplications) BeansUtils.getBean(Constants._beans_hydra_applications);
		String urlPrefix =  inMessage.getData().get(IMessage._url_scheme) 
			+ "://" + inMessage.getData().get(IMessage._url_server_name);
			
		WebApplication webApplication = webApplications.getValidAppliction(urlPrefix);
		if(webApplication == null){
			_log.fatal("Could not find application ID for: " + urlPrefix);
			return;
		}
		// 2. set other data
		inMessage.getData().put(IMessage._app_id, webApplication.getId());			
		inMessage.getData().put(IMessage._url_scheme, inWebContext.getHttpServletRequest().getScheme());
		inMessage.getData().put(IMessage._url_server_name, inWebContext.getHttpServletRequest().getServerName());
		inMessage.getData().put(IMessage._url_server_port, "" + inWebContext.getHttpServletRequest().getLocalPort());
		
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
		if(inMessage != null && inMessage.getData() != null)
			for(Object key:inMessage.getData().keySet().toArray()) 
				inMessage.getData().remove(key);
	}
	
}
