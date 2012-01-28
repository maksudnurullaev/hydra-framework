package org.hydra.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplication;
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
	public static Result setApplicationData(
			Result inResult,
			CommonMessage inMessage,
			WebContext context) {
		// 1. set web context
		if (context == null) {
			inResult.setErrorString("Could not find web context!");
			inResult.setResult(false);
			return inResult;
		}
		// 1.1 context path
		inMessage.setContextPath(context.getServletContext().getContextPath());
		// 2. set web application
		setWebAppParameters(inResult, inMessage, context);
		if (!inResult.isOk())
			return inResult;
		// 3. set session id
		inMessage.setSessionID(context.getSession().getId());
		// 4. set locale
		getSessionData(inResult, inMessage, Constants._session_locale, context);
		if(inResult.isOk()){
			inMessage.setLocale((String) inResult.getObject());
		}else{
			inMessage.setLocale(inMessage.getData().get("default_locale"));
		}
		// 5. set session user id
		SessionUtils.getSessionData(inResult, inMessage, Constants._session_user_id, context);
		if(inResult.isOk()){
			inMessage.setUserId((String) inResult.getObject());
		}
		
		inResult.setResult(true);
		return inResult;
	};
	
	public static void setWebAppParameters(
			Result inResult,
			CommonMessage inMessage, 
			WebContext context) {
		BeansUtils.getWebContextBean(inResult,
				Constants._bean_hydra_web_applications);
		if (!inResult.isOk() || !(inResult.getObject() instanceof WebApplications))
			return;		
		WebApplications webApplications = (WebApplications) inResult.getObject();

		// 1. validate mode
		String urlString = inMessage.getUrl();
		if(urlString != null){
			int found = urlString.indexOf("mode=");
			WebApplication app = webApplications.getValidApplication4(
					(found != -1) ? urlString.toLowerCase().substring(found) 
							: urlString);	
			if(app != null){
				inMessage.getData().put("appid", app.getId());
				inMessage.getData().put("locale", app.getDefaultLocale());
				inMessage.setTimeout(app.getTimeout());
				inResult.setResult(true);
				return;
			}				
		}else{
			inResult.setErrorString("Could not find _URL parameter for message!");
		}
	};

	public static void setSessionData(
			Result inResult,
			CommonMessage inMessage,
			String inKey,
			Object inValue,
			WebContext context) {
		context.getSession().setAttribute(
				(inMessage.getData().get("appid") +  inKey),
				inValue);
	};
	
	public static void getSessionData(
			Result inResult,
			CommonMessage inMessage,
			String inKey,
			WebContext context) {
		
		String sessionKey = inMessage.getData().get("appid") + inKey;
		_log.debug("Try to get session data by key: " + sessionKey);		
		String sessionValue = (String) context.getSession().getAttribute(sessionKey);
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

	public static boolean validateCaptcha(CommonMessage inMessage, WebContext context) {
		try{
			HttpSession session = context.getSession();
			int sessionValue = (Integer) session.getAttribute(inMessage.getData().get("appid") + Constants._captcha_value);
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
