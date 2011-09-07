package org.hydra.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.hydra.beans.WebApplications;
import org.hydra.messages.CommonMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");
	public static Pattern pattern = Pattern.compile("appid=(\\w+)&uuid=");    		
	

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
			inResult.setErrorString("Could not find web context!");
			inResult.setResult(false);
			return inResult;
		}
		// 2. set web application
		setWebAppId(inResult, inMessage, inMessage._web_context);
		if (!inResult.isOk())
			return inResult;
		// 3. set session id
		inMessage.setSessionID(inMessage._web_context.getSession().getId());
		// 4. set locale
		getSessionData(inResult, inMessage, Constants._session_locale);
		if(inResult.isOk()){
			inMessage._locale = (String) inResult.getObject();
		}else{
			inMessage._locale = inMessage._web_application.getDefaultLocale();
		}
		// 5. set session user id
		SessionUtils.getSessionData(inResult, inMessage, Constants._session_user_id);
		if(inResult.isOk()){
			inMessage._user_id = (String) inResult.getObject();
		}
		
		inResult.setResult(true);
		return inResult;
	};
	
	private static void generateSessionDataKey(
			Result inResult,
			String inAppId, 
			String inKey) {
		if (inAppId == null || inKey == null) {
			inResult.setObject("Could not generate Session Data Key!");
			_log.warn("Could not generate Session Data Key!");
			inResult.setResult(false);
			return;
		}
		inResult.setObject(inAppId + inKey);
		inResult.setResult(true);
	};

	public static void setWebAppId(
			Result inResult,
			CommonMessage inMessage, 
			WebContext inWebContext) {
		if (inMessage == null || inWebContext == null) {
			inResult.setErrorString("CommonMessage or WebContext equal NULL!");
			inResult.setResult(false);
			return;
		}
		
		BeansUtils.getWebContextBean(inResult,
				Constants._bean_hydra_web_applications);
		if (!inResult.isOk() || !(inResult.getObject() instanceof WebApplications))
			return;		
		WebApplications webApplications = (WebApplications) inResult.getObject();

		// 1. test for mode
		String urlString = inMessage.getUrl();
		if(urlString != null){
			URL url = null;
			try {
				url = new URL(urlString);
				if(url != null
						&& url.getQuery() != null 
						&& url.getQuery().contains("mode=")){
					System.out.println("test for mode: " + url.getQuery());
					inMessage._web_application = webApplications.getValidApplication4(url.getQuery());	
					if(inMessage._web_application != null){
						System.out.println("test for mode #2");
						inResult.setResult(true);
						return;
					}
				}
			} catch (MalformedURLException e) {
				_log.error(e.getMessage());
				inResult.setErrorString(e.getMessage());
				return ;
			}
			// 2. test for real url
			System.out.println("test for real url");
			inMessage._web_application = webApplications.getValidApplication4(urlString);
			if (inMessage._web_application == null) {
				inResult.setErrorString("Could not initialize WebApplication object!");
			} else {
				inResult.setResult(true);
			}
		}else{
			inResult.setErrorString("Could not find _URL parameter for message!");
		}
	};

	public static void setSessionData(
			Result inResult,
			CommonMessage inCommonMessage,
			String inKey,
			Object inValue) {
		generateSessionDataKey(inResult, inCommonMessage._web_application.getId(), inKey);
		if(!inResult.isOk()) return;
		String sessionKey = (String) inResult.getObject();
		if (inCommonMessage == null
					|| inCommonMessage._web_context == null
					|| inCommonMessage._web_application == null
					|| inKey == null
			) {
			inResult.setResult(false);
			inResult.setErrorString("Invalid session!");
		} else {
			inCommonMessage._web_context.getSession().setAttribute(
					sessionKey,
					inValue);
			inResult.setResult(true);
		}
	};
	
	public static void getSessionData(
			Result inResult,
			CommonMessage inMessage,
			String inKey) {
		generateSessionDataKey(inResult, inMessage._web_application.getId(), inKey);
		if (!inResult.isOk()){
			inResult.setErrorString("Could not generate unique session ID");
			inResult.setResult(false);
			return;
		}
		
		String sessionKey = (String) inResult.getObject();
		_log.debug("Try to get session data by key: " + sessionKey);		
		String sessionValue = (String) inMessage._web_context.getSession()
				.getAttribute(sessionKey);
		if (sessionValue == null){
			_log.info("Could not get session session data for key: " + sessionKey);
			inResult.setResult(false);
			return;
		}

		inResult.setObject(sessionValue);
		inResult.setResult(true);
	}

	public static String getCaptchaId(String queryString) {
    	Matcher m = pattern.matcher(queryString);
    	if(m.matches())
    		return m.group(1);
		return null;
	}

}
