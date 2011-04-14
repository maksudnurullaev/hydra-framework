package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
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
	public static Result setSessionData(MessageBean inMessage, WebContext inWebContext) {
		Result result = new Result();
		// set web context
		inMessage._web_context = WebContextFactory.get();
		// set web application
		WebApplications webApplications = (WebApplications) BeansUtils.getBean(Constants._beans_hydra_applications);
		String urlPrefix =  inMessage._web_context.getHttpServletRequest().getScheme() 
			+ "://" + inMessage._web_context.getHttpServletRequest().getServerName();
		// test message
		inMessage._web_application = webApplications.getValidApplication(urlPrefix);
		
		if(inMessage._web_application == null ||
				inMessage._web_context == null){
			_log.fatal("Could not find web application for: " + urlPrefix);
			result.setResult("Could not find web application for: " + urlPrefix);
			result.setResult(false);
			return result;
		}
		// set locale if exist
		if(inMessage._web_context.getSession().getAttribute(
				inMessage._web_application.getId() + Constants._data_locale) != null){
			_log.debug("Set preserved locale session's locale: " + inMessage._web_context.getSession().getAttribute(
					inMessage._web_application.getId() + Constants._data_locale));
			inMessage._locale = (String) inMessage._web_context.getSession().getAttribute(
					inMessage._web_application.getId() + Constants._data_locale);
		}else{
			_log.debug("Set default locale to session: " 
					+ MessagesManager.getTextManager().getDefaultLocale());
			inMessage._locale = MessagesManager.getTextManager().getDefaultLocale();
		}
		// set group id
		inMessage._session_id = inMessage._web_context.getSession().getId();
		
		result.setResult(true);
		return result;
	}
	/**
	 * @param inMessage
	 */
	public static void removeSessionData(IMessage inMessage) {
		if(inMessage != null && inMessage.getData() != null)
			for(Object key:inMessage.getData().keySet().toArray()) 
				inMessage.getData().remove(key);
	}
	
}
