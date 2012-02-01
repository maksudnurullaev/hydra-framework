package org.hydra.utils;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
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
			WebContext inWebCotext) {
		ServletContext context = inWebCotext.getServletContext();
		// Set session ID
		inMessage.setSessionID(inWebCotext.getSession().getId());
		// Context path
		inMessage.setContextPath(context.getContextPath());
		// Set web application's data 
		setWebAppParameters(inResult, inMessage, context);
		if (!inResult.isOk())
			return inResult;
		
		inResult.setResult(true);
		return inResult;
	};
	
	public static void setWebAppParameters(
			Result inResult,
			CommonMessage inMessage,
			ServletContext inContext) {
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
				inMessage.getData().put("_appid", app.getId());
				inMessage.getData().put("_user", getSessionData(inContext, "_user", app.getId()));
				inMessage.getData().put("_context_path", inContext.getContextPath());
				inMessage.setTimeout(app.getTimeout());
				inResult.setResult(true);
				if(inMessage.getData().containsKey("_locale")) return; // not need to init locale state
				if(isContextContain(inContext, "_locale", app.getId())){
					inMessage.getData().put("_locale", getSessionData(inContext, "_locale", app.getId()));				
				}else{
					inMessage.getData().put("_locale", app.getDefaultLocale());				
				}
				return;
			}
			
		}else{
			inResult.setErrorString("Could not find _URL parameter for message!");
		}
	};

	public static void setSessionData(
			CommonMessage inMessage,
			String inKey,
			Object inValue,
			WebContext context) {
		String sessionDataKey = inMessage.getData().get("_appid") +  inKey;
		context.getSession().setAttribute(sessionDataKey, inValue);
		_log.error("sessionKey: " + sessionDataKey);
		_log.error("context.getSession().getAttribute(sessionKey): " + context.getSession().getAttribute(sessionDataKey));
	};
	
	public static String getSessionData(ServletContext inContext, String inKey, String inAppId){
		String sessionDataKey = inAppId +  inKey;		
		_log.error("sessionKey: " + sessionDataKey);
		_log.error("context.getSession().getAttribute(sessionKey): " + inContext.getAttribute(sessionDataKey));
		return ((String)inContext.getAttribute(sessionDataKey));
	}
	
	public static boolean isContextContain(ServletContext inContext, String inKey, String inAppId){
		Enumeration<String> enumerator = inContext.getAttributeNames();
		while(enumerator.hasMoreElements()){
			String key = enumerator.nextElement();
			if(key.equals(inAppId + inKey)) return true;
		}
		return(false);
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
			int sessionValue = (Integer) session.getAttribute(inMessage.getData().get("_appid") + Constants._captcha_value);
			if(inMessage.getData().containsKey(Constants._captcha_value)){
				String captchaValue = inMessage.getData().get(Constants._captcha_value);
				int passedValue = Integer.parseInt(captchaValue);
				if(passedValue == sessionValue){
					inMessage.getData().put(Constants._captcha_value, Constants._captcha_OK);
					return(true);
				}
			}
		}catch (Exception e){
			_log.error(e.getMessage());
		}
		return false;
	}

	public static boolean isCaptchaVerified(CommonMessage inMessage) {
		if(inMessage.getData().containsKey(Constants._captcha_value)){
			String value = inMessage.getData().get(Constants._captcha_value);
			return(value.equalsIgnoreCase(Constants._captcha_OK));
		}
		return false;
	}	
	
	public static void printSessionData(WebContext webContext, CommonMessage inMessage){
		System.out.println("#### SESSION DATA for: " + inMessage.getData().get("_appid"));
		Enumeration<String> keys = webContext.getSession().getAttributeNames();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			System.out.println(String.format("%s: %s", webContext.getSession().getAttribute(key)));
		}		
		System.out.println("#### END ####");				
	}
}
