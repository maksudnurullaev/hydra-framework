package org.hydra.utils;

import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.deployers.Dictionary;
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
		if(inWebContext.getSession() != null){
			inMessage.setSession(inWebContext.getSession());
			inMessage.setSessionId(inWebContext.getSession().getId());
		}else{
			inResult.setErrorString("Could not setup web session!");
			return(inResult);
		}
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
			setSessionVariables(inResult, app, inMessage, inWebContext);
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
		inMessage.getData().put(Constants._appid_key, app.getId());
		// set application timeout
		inMessage.setTimeout(app.getTimeout());
		inResult.setResult(true);
	};

	public static void setSessionVariables(
			Result inResult,
			WebApplication inApp,
			IMessage inMessage,
			WebContext inContext) {

		if(inContext != null){
			// check for session browser value
			if(!isSessionDataExist(inContext, Constants._browser_key, inApp.getId())){
				inMessage.setError("Could not setup web session!");
				inMessage.setReloadPage(true);
				return;
			}
			inMessage.getData().put(Constants._browser_key,getSessionData(inContext, Constants._browser_key, inApp.getId()));
			// check for user (optional)
			inMessage.getData().put(Constants._userid_key, getSessionData(inContext, Constants._userid_key, inApp.getId()));
			inMessage.getData().put(Constants._roles_key, getSessionData(inContext, Constants._roles_key, inApp.getId()));
			if(Utils.getMessageDataOrNull(inMessage, Constants._locale_key) != null){ 
				return; // not need to init locale state
			}
			// check for locale (optional)
			if(isContextContain(inContext, Constants._locale_key, inApp.getId())){
				inMessage.getData().put(Constants._locale_key, getSessionData(inContext, Constants._locale_key, inApp.getId()));				
			}else{
				inMessage.getData().put(Constants._locale_key, Dictionary.getDefaultLocale());				
			}
		} else{
			inResult.setErrorString("Could not find web context!");		
		}
	}
	
	public static void setSessionData(
			HttpSession inSession, 
			String inKey, 
			String inAppId, 
			Object inValue) {
		String key = getAppAndKeyID(inAppId,inKey);
		inSession.setAttribute(key, inValue);
		_log.debug("sessionKey: " + key);
		_log.debug("sessionData: " + inSession.getAttribute(key));
	};
	
	public static String getSessionData(WebContext inContext, String inKey, String inAppId){
		String key = getAppAndKeyID(inAppId,inKey);
		return ((String)inContext.getSession().getAttribute(key));
	}
	
	public static String getSessionData(HttpSession inContext, String inKey, String inAppId){
		String key = getAppAndKeyID(inAppId,inKey);
		return ((String)inContext.getAttribute(key));
	}	
	
	public static boolean isSessionDataExist(WebContext inContext, String inKey, String inAppId){
		String key = getAppAndKeyID(inAppId,inKey);
		return (inContext.getSession().getAttribute(key) != null);
	}	
	public static boolean isContextContain(WebContext inContext, String inKey, String inAppId){
		String realKey = getAppAndKeyID(inAppId,inKey);
		return(inContext.getSession().getAttribute(realKey) != null);
	}

	public static String getAppAndKeyID(String inAppId, String inKey){
		return(inAppId + '.' + inKey);
	}
	
	public static String getSessionData(IMessage inMessage){
		if(inMessage == null || inMessage.getSession() == null){
			_log.debug("Error: invalid session objects!");
			return("Error: invalid session objects!");
		}
		HttpSession session = inMessage.getSession();
		StringBuffer sb = new StringBuffer();
		sb.append("#### SESSION DATA for: " + Utils.getMessageDataOrNull(inMessage, Constants._appid_key) + '\n');
		sb.append("SESSION ID: " + session.getId() + '\n');
		Enumeration<String> keys = session.getAttributeNames();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			sb.append(String.format("%s: %s", key, session.getAttribute(key)) + '\n');
		}		
		sb.append("#### END ####" + '\n');
		return(sb.toString());
	}
	
	public static boolean isMobileBrowser(HttpServletRequest request){
		if( request == null ){
			_log.fatal("request == null");
			return(false);
		}
		String url = request.getRequestURL().toString() + (request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
		if(url.toLowerCase().contains("http://wap")){
			return (true);
		}else if(request.getHeader("User-Agent") == null){
			_log.warn("request.getHeader(\"User-Agent\") == null");
			return(false);			
		}
		return(isMobileBrowser(request.getHeader("User-Agent")));
	}
	
	public static boolean isMobileBrowser(String ua){
		if(ua == null){ 
			_log.debug("NO User-Agent string found for browser detection!");
			return(false);
		}
		ua = ua.toLowerCase();
		_log.debug("Incoming User-Agent string: " + ua);
		if(ua.matches(".*(android.+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*")||ua.substring(0,4).matches("1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|e\\-|e\\/|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|xda(\\-|2|g)|yas\\-|your|zeto|zte\\-")) {
			_log.debug("... mobile browser detected!");
		  return(true);
		}
		_log.debug("... mobile browser not found!");
		return(false);
	}	
}
