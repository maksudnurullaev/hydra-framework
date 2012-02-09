package org.hydra.utils;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.interfaces.IMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");
	public static Pattern pattern = Pattern.compile("appid=(\\w+).*");    		

	public static Result setApplicationData(
			Result inResult,
			IMessage inMessage,
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
			IMessage inMessage,
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
			WebApplication app = null;
			if(found != -1){
				app = webApplications.getValidApplication4(
						urlString.toLowerCase().substring(found) 
								);
			}
			
			if(app == null) // if still null
				app = webApplications.getValidApplication4(urlString);
			
			if(app == null) // if still null
				app = webApplications.getValidApplication4("hydra.uz");				
			
			inMessage.getData().put("_appid", app.getId());
			if(!inMessage.getData().containsKey("appid"))
				inMessage.getData().put("appid", app.getId());

			inMessage.getData().put("_user", getSessionData(inContext, "_user", app.getId()));
			inMessage.setTimeout(app.getTimeout());
			inResult.setResult(true);
			if(inMessage.getData().containsKey("_locale")) return; // not need to init locale state
			if(isContextContain(inContext, "_locale", app.getId())){
				inMessage.getData().put("_locale", getSessionData(inContext, "_locale", app.getId()));				
			}else{
				inMessage.getData().put("_locale", app.getDefaultLocale());				
			}
			return;
			
		}else{
			inResult.setErrorString("Could not find _URL parameter for message!");
		}
	};

	public static void setSessionData(ServletContext inContext, String inKey, String inAppId, Object inValue) {
		String sessionDataKey = inAppId + inKey;
		inContext.setAttribute(sessionDataKey, inValue);
		_log.debug("sessionKey: " + sessionDataKey);
		_log.debug("sessionData: " + inContext.getAttribute(sessionDataKey));
	};
	
	public static String getSessionData(ServletContext inContext, String inKey, String inAppId){
		String sessionDataKey = inAppId +  inKey;		
		_log.debug("sessionKey: " + sessionDataKey);
		_log.debug("sessionData: " + inContext.getAttribute(sessionDataKey));
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

	public static void printSessionData(WebContext webContext, CommonMessage inMessage){
		System.out.println("#### SESSION DATA for: " + inMessage.getData().get("appid"));
		Enumeration<String> keys = webContext.getSession().getAttributeNames();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			System.out.println(String.format("%s: %s", webContext.getSession().getAttribute(key)));
		}		
		System.out.println("#### END ####");				
	}
}
