package org.hydra.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplications;
import org.hydra.messages.CommonMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");
	public static Pattern pattern = Pattern.compile("appid=(\\w+).*");    		
	

	/**
	 * Attache session data (locale, userId and etc.)
	 * 
	 * @param inMessage
	 * @param inSession
	 */
	public static Result attachSessionData(
			Result inResult,
			CommonMessage inMessage) {
		// 1. set web context
		WebContext context = inMessage.getWebContext();
		if (context == null) {
			inResult.setErrorString("Could not find web context!");
			inResult.setResult(false);
			return inResult;
		}
		// 2. set web application
		setWebApp(inResult, inMessage);
		if (!inResult.isOk())
			return inResult;
		// 3. set session id
		inMessage.setSessionID(context.getSession().getId());
		// 4. set locale
		getSessionData(inResult, inMessage, Constants._session_locale);
		if(inResult.isOk()){
			inMessage.setLocale((String) inResult.getObject());
		}else{
			inMessage.setLocale(inMessage.getWebApplication().getDefaultLocale());
		}
		// 5. set session user id
		SessionUtils.getSessionData(inResult, inMessage, Constants._session_user_id);
		if(inResult.isOk()){
			inMessage.setUserId((String) inResult.getObject());
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

	public static void setWebApp(
			Result inResult,
			CommonMessage inMessage) {
		BeansUtils.getWebContextBean(inResult,
				Constants._bean_hydra_web_applications);
		if (!inResult.isOk() || !(inResult.getObject() instanceof WebApplications))
			return;		
		WebApplications webApplications = (WebApplications) inResult.getObject();

		// 1. validate mode
		String urlString = inMessage.getUrl();
		if(urlString != null){
			int found = urlString.indexOf("mode=");
			if(found != -1){
				inMessage.setWebApplication(webApplications.getValidApplication4(urlString.toLowerCase().substring(found)));	
				if(inMessage.getWebApplication() != null){
					inResult.setResult(true);
					return;
				}				
			}
			inMessage.setWebApplication(webApplications.getValidApplication4(urlString));
			if (inMessage.getWebApplication() == null) {
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
			CommonMessage inMessage,
			String inKey,
			Object inValue) {
		WebContext context = inMessage.getWebContext();
		generateSessionDataKey(inResult, inMessage.getWebApplication().getId(), inKey);
		if(!inResult.isOk()) return;
		String sessionKey = (String) inResult.getObject();
		if (inMessage == null
					|| inMessage.getWebApplication() == null
					|| inKey == null
			) {
			inResult.setResult(false);
			inResult.setErrorString("Invalid session!");
		} else {
			context.getSession().setAttribute(
					sessionKey,
					inValue);
			inResult.setResult(true);
		}
	};
	
	public static void getSessionData(
			Result inResult,
			CommonMessage inMessage,
			String inKey) {
		generateSessionDataKey(inResult, inMessage.getWebApplication().getId(), inKey);
		if (!inResult.isOk()){
			inResult.setErrorString("Could not generate unique session ID");
			inResult.setResult(false);
			return;
		}
		WebContext context = inMessage.getWebContext();
		String sessionKey = (String) inResult.getObject();
		_log.debug("Try to get session data by key: " + sessionKey);		
		String sessionValue = (String) context.getSession()
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

	public static boolean validateCaptcha(CommonMessage inMessage) {
		WebContext context = inMessage.getWebContext();
		try{
			HttpSession session = context.getSession();
			int sessionValue = (Integer) session.getAttribute(inMessage.getWebApplication().getId() + Constants._captcha_value);
			if(inMessage.getData().containsKey(Constants._captcha_value)){
				String captchaValue = inMessage.getData().get(Constants._captcha_value);
				int passedValue = Integer.parseInt(captchaValue);
				if(passedValue == sessionValue)	return(true);
			}
		}catch (Exception e){
			_log.error(e.getMessage());
		}
		return false;
	}

}
