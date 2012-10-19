package org.hydra.deployers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

public final class System {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.System");
	
	public static String getWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			IMessage inMessage
			) {
		
		if(inWhat.compareToIgnoreCase("LanguageBar") == 0)
			return getSystemLanguagebarKeyHow(inKey, inHow, inMessage);
		if(inWhat.compareToIgnoreCase("Login") == 0)
			return SystemLogin.getKeyHow(inKey, inHow, inMessage);
		if(inWhat.compareToIgnoreCase("Password") == 0)
			return SystemPassword.getKeyHow(inKey, inHow, inMessage);		
		if(inWhat.compareToIgnoreCase("Captcha") == 0)
			return SystemCaptcha.getKeyHow(inKey, inHow, inMessage);
		if(inWhat.compareToIgnoreCase("Session") == 0)
			return SystemSession.getKeyHow(inKey, inHow, inMessage);
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}
	
	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow,
			IMessage inMessage
			) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inMessage);
		
		String tempStr = String.format("{{System|Languagebar|%s|%s}}",inKey, inHow);
		_log.error("Could not find HOW part for: " + tempStr);
		return tempStr ;
	}

	private static String getSystemLanguagebarKeyA(
			String inKey,
			IMessage inMessage) {
		Map<String, String> appLocales = getAppDefinedLocales(inMessage);
		if(appLocales != null && appLocales.size() > 0){
			String resultStr = "";
			for (Map.Entry<String, String> entry:appLocales.entrySet()) {
				if(!resultStr.isEmpty())
					resultStr += "&nbsp;&nbsp";
				if(entry.getKey().compareToIgnoreCase(inMessage.getData().get("locale")) == 0){ 
					resultStr += entry.getValue();
				}else{
					resultStr += Utils.T("template.html.a.language.bar", entry.getKey(), entry.getValue());
				}
			}
			return resultStr;
		}
		_log.error("Could not define locale for:" + inMessage.getData().get("appid"));
		return ("Could not define locale for:" + inMessage.getData().get("appid"));
	}

	public static Map<String, String> getAppDefinedLocales(IMessage inMessage){
		if(inMessage.getData() == null || inMessage.getData().get("appid") == null){
			return(null);
		}
		Result result = new Result();
		BeansUtils.getWebContextBean(result, (inMessage.getData().get("appid") + Constants._bean_web_app_id_postfix));
		if(result.isOk() && result.getObject() instanceof WebApplication){ 
			WebApplication app = (WebApplication) result.getObject();
			return(app.getLocales());
		}
		return (null);
	}
}
