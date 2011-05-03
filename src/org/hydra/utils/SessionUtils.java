package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.hydra.beans.WebApplications;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");

	/**
	 * Attache session data (locale, userId and etc.)
	 * 
	 * @param inMessage
	 * @param inSession
	 */
	public static Result attachSessionData(
			Result inResult,
			CommonMessage inMessage, 
			WebContext inWebContext) {
		// 1. set web context
		inMessage._web_context = WebContextFactory.get();
		if (inMessage._web_context == null) {
			inResult.setResult("Could not find web context!");
			inResult.setResult(false);
			return inResult;
		}
		// 2. set web application
		setWebApplication(inResult, inMessage, inMessage._web_context);
		if (!inResult.isOk())
			return inResult;
		// 3. set session id
		inMessage._session_id = inMessage._web_context.getSession().getId();
		// 4. set locale
		getSessionData(inResult, inMessage, Constants._session_locale);
		if(inResult.isOk()){
			inMessage._locale = (String) inResult.getObject();
		}else{
			inMessage._locale = MessagesManager.getTextManager().getDefaultLocale();
		}
		// 5. set URL Object
		getSessionURLWrapper(inResult, inMessage);
		
		inResult.setResult(true);
		return inResult;
	};
	
	public static void getSessionURLWrapper(Result inResult, CommonMessage inMessage) {
		getSessionData(inResult, inMessage, Constants._session_url);
		if(inResult.isOk())
			inMessage._moder = new Moder((String) inResult.getObject());
	};
	public static void setSessionURLWrapper(Result result, CommonMessage inMessage) {
		SessionUtils.setSessionData(result , inMessage, Constants._session_url, inMessage.getData().get(Constants._session_url));
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			result.setResult(false);		
		}
	};
	
	private static void generateSessionDataKey(
			Result inResult,
			CommonMessage inCommonMessage, 
			String inKey) {
		if (inCommonMessage._web_application == null || inKey == null) {
			inResult.setObject("Could not generate Session Data Key!");
			_log.warn("Could not generate Session Data Key!");
			inResult.setResult(false);
			return;
		}
		inResult.setObject(inCommonMessage._web_application.getId() + inKey);
		inResult.setResult(true);
	};

	public static void setWebApplication(
			Result inResult,
			CommonMessage inMessage, 
			WebContext inWebContext) {
		if (inMessage == null || inWebContext == null) {
			inResult.setResult("CommonMessage or WebContext equal NULL!");
			inResult.setResult(false);
			return;
		}

		String urlPrefix = inWebContext.getHttpServletRequest().getScheme()
				+ "://" + inWebContext.getHttpServletRequest().getServerName();

		BeansUtils.getWebContextBean(inResult,
				Constants._beans_hydra_applications);
		if (!inResult.isOk() || !(inResult.getObject() instanceof WebApplications))
			return;

		WebApplications webApplications = (WebApplications) inResult.getObject();
		inMessage._web_application = webApplications.getValidApplication(urlPrefix);

		if (inMessage._web_application == null) {
			inResult.setResult("Could not initialize WebApplication object!");
			inResult.setResult(false);
		} else
			inResult.setResult(true);
	};

	public static void setSessionData(
			Result inResult,
			CommonMessage inCommonMessage,
			String inKey,
			Object inValue) {
		generateSessionDataKey(inResult, inCommonMessage, inKey);
		if(!inResult.isOk()) return;
		String sessionKey = (String) inResult.getObject();
		if (inCommonMessage == null
					|| inCommonMessage._web_context == null
					|| inCommonMessage._web_application == null
					|| inKey == null
					|| inValue == null) {
			inResult.setResult(false);
			inResult.setResult("Invalid session!");
		} else {
			inCommonMessage._web_context.getSession().setAttribute(
					sessionKey,
					inValue);
			inResult.setResult(true);
		}
	};
	
	public static void getSessionData(
			Result inResult,
			CommonMessage inCommonMessage,
			String inKey) {
		generateSessionDataKey(inResult, inCommonMessage, inKey);
		if (!inResult.isOk()){
			inResult.setResult("Could not generate unique session ID");
			inResult.setResult(false);
			return;
		}
		
		String sessionKey = (String) inResult.getObject();
		_log.debug("Try to get session data by key: " + sessionKey);		
		String sessionValue = (String) inCommonMessage._web_context.getSession()
				.getAttribute(sessionKey);
		if (sessionValue == null){
			_log.warn("Could not get session session data for key: " + sessionKey);
			inResult.setResult(false);
			return;
		}

		inResult.setObject(sessionValue);
		inResult.setResult(true);
	}


}
