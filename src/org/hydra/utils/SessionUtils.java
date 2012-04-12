package org.hydra.utils;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.messages.interfaces.IMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");
	public static Pattern pattern = Pattern.compile("appid=(\\w+).*");    		

	public static Result setApplicationData(
			Result inResult,
			IMessage inMessage,
			WebContext inWebContext) {
		ServletContext context = inWebContext.getServletContext();
		// set session ID
		inMessage.setSessionID(inWebContext.getSession().getId());
		// set context path
		inMessage.setContextPath(context.getContextPath());
		// setup web application
		String urlString = inMessage.getUrl();
		WebApplication app = null;		
		
		if(urlString != null){
			app = getWebApplication(urlString);
			// Set web application's data 
			setWebAppParameters(inResult, inMessage, app);
			if (!inResult.isOk())
				return inResult;
			// Set web application session parameters
			setWebAppSessionValues(inResult, app, inMessage, inWebContext);
			if (!inResult.isOk())
				return inResult;
		} else {
			inResult.setErrorString("Could not find _URL parameter for message!");
			return(inResult);
		}
		inResult.setResult(true);
		return inResult;
	};
	
	public static WebApplication getWebApplication(String inUrlString){
		Result inResult = new Result();
		WebApplication app = null;
		BeansUtils.getWebContextBean(inResult,
				Constants._bean_hydra_web_applications);
		
		if (!inResult.isOk() || !(inResult.getObject() instanceof WebApplications))
			return(null);		
		
		WebApplications webApplications = (WebApplications) inResult.getObject();

		// 1. validate mode
		if(inUrlString != null){
			int found = inUrlString.indexOf(Constants._url_mode_param);
			if(found != -1){
				String mode_str = inUrlString.toLowerCase().substring(found + Constants._url_mode_param.length());
				app = webApplications.getValidApplication4(mode_str);
			}
			
			if(app == null) { // if still null
				_log.debug("Mode not found!");
				app = webApplications.getValidApplication4(inUrlString);
			}
			
			if(app == null) { // if still null
				_log.warn("Valid domain name not found, use default hyhdra.uz!");
				app = webApplications.getValidApplication4("hydra.uz");
			}
		}
		return(app);
	}
	
	public static void setWebAppParameters(
			Result inResult,
			IMessage inMessage,
			WebApplication app) {
		// set application id should be fine
		inMessage.getData().put("_appid", app.getId());
		if(!inMessage.getData().containsKey("appid"))
			inMessage.getData().put("appid", app.getId());
		// set application timeout
		inMessage.setTimeout(app.getTimeout());
		inResult.setResult(true);
	};

	public static void setWebAppSessionValues(
			Result inResult,
			WebApplication inApp,
			IMessage inMessage,
			WebContext inContext) {

		if(inContext != null){
			inMessage.getData().put("_user", getSessionData(inContext, "_user", inApp.getId()));
			if(inMessage.getData().containsKey("_locale")) return; // not need to init locale state
			if(isContextContain(inContext, "_locale", inApp.getId())){
				inMessage.getData().put("_locale", getSessionData(inContext, "_locale", inApp.getId()));				
			}else{
				inMessage.getData().put("_locale", inApp.getDefaultLocale());				
			}
		} else{
			inResult.setErrorString("Could not find web context!");		
		}
	}
	
	public static void setSessionData(WebContext inContext, String inKey, String inAppId, Object inValue) {
		String sessionDataKey = inAppId + inKey;
		inContext.getSession().setAttribute(sessionDataKey, inValue);
		_log.debug("sessionKey: " + sessionDataKey);
		_log.debug("sessionData: " + inContext.getSession().getAttribute(sessionDataKey));
	};
	
	public static String getSessionData(WebContext inContext, String inKey, String inAppId){
		String sessionDataKey = inAppId +  inKey;		
		_log.debug("sessionKey: " + sessionDataKey);
		_log.debug("sessionData: " + inContext.getSession().getAttribute(sessionDataKey));
		return ((String)inContext.getSession().getAttribute(sessionDataKey));
	}
	
	public static boolean isContextContain(WebContext inContext, String inKey, String inAppId){
		Enumeration<String> enumerator = inContext.getSession().getAttributeNames();
		while(enumerator.hasMoreElements()){
			String key = enumerator.nextElement();
			if(key.equals(inAppId + inKey)) return true;
		}
		return(false);
	}

	public static void printSessionData(WebContext webContext, IMessage inMessage){
		System.out.println("#### SESSION DATA for: " + 	inMessage.getData().get("appid"));
		System.out.println("SESSION ID: " + webContext.getSession().getId());
		Enumeration<String> keys = webContext.getSession().getAttributeNames();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			System.out.println(String.format("%s: %s", key, webContext.getSession().getAttribute(key)));
		}		
		System.out.println("#### END ####");				
	}	
}
