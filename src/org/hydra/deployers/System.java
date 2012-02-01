package org.hydra.deployers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.messages.CommonMessage;
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
			CommonMessage inMessage
			) {
		
		if(inWhat.compareToIgnoreCase("LanguageBar") == 0)
			return getSystemLanguagebarKeyHow(inKey, inHow, inMessage);
		if(inWhat.compareToIgnoreCase("Login") == 0)
			return SystemLogin.getKeyHow(inKey, inHow, inMessage);
		if(inWhat.compareToIgnoreCase("Captcha") == 0)
			return SystemCaptcha.getKeyHow(inKey, inHow, inMessage);
		if(inWhat.compareToIgnoreCase("Tagger") == 0)
			return SystemTagger.getKeyHow(inKey, inHow, inMessage);
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}
	
	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow,
			CommonMessage inMessage
			) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inMessage);
		
		String tempStr = String.format("{{System|Languagebar|%s|%s}}",inKey, inHow);
		_log.error("Could not find HOW part for: " + tempStr);
		return tempStr ;
	}

	private static String getSystemLanguagebarKeyA(
			String inKey,
			CommonMessage inMessage) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, (inMessage.getData().get("_appid") + Constants._bean_web_app_id_postfix));
		if(result.isOk() && result.getObject() instanceof WebApplication){ // generate language bar
			WebApplication app = (WebApplication) result.getObject();
			String resultStr = "";
			for (Map.Entry<String, String> entry:app.getLocales().entrySet()) {
				if(entry.getKey().compareToIgnoreCase(inMessage.getData().get("_locale")) == 0){ // selected
					resultStr += entry.getValue();
				}else{
					resultStr += Utils.T("template.html.a.language.bar", entry.getKey(), entry.getValue());
				}
				if(!resultStr.isEmpty())
					resultStr += "&nbsp;&nbsp;";
			}
			return resultStr;
		}
		_log.error("Could not define locale for:" + inMessage.getData().get("_appid"));
		return ("Could not define locale for:" + inMessage.getData().get("_appid"));
	}

}
