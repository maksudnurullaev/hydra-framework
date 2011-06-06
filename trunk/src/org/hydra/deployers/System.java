package org.hydra.deployers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;

public final class System {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.System");
	
	public static String getWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID, 
			String inUserID) {
		
		if(inWhat.compareToIgnoreCase("LanguageBar") == 0)
			return getSystemLanguagebarKeyHow(inKey, inHow, inLocale, inApplicationID);
		if(inWhat.compareToIgnoreCase("Login") == 0)
			return SystemLogin.getKeyHow(inKey, inHow, inLocale, inApplicationID, inUserID);
		
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}
	
	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inLocale, inApplicationID);
		
		String tempStr = String.format("{{System|Languagebar|%s|%s}}",inKey, inHow);
		_log.error("Could not find HOW part for: " + tempStr);
		return tempStr ;
	}

	private static String getSystemLanguagebarKeyA(
			String inKey, // IGNORE 
			String inLocale, 
			String inApplicationID) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, (inApplicationID + Constants._bean_web_app_id_postfix));
		if(result.isOk() && result.getObject() instanceof WebApplication){ // generate language bar
			WebApplication app = (WebApplication) result.getObject();
			String resultStr = "";
			for (Map.Entry<String, String> entry:app.getLocales().entrySet()) {
				if(entry.getKey().compareToIgnoreCase(inLocale) == 0){ // selected
					resultStr += entry.getValue();
				}else{
					resultStr += String.format(Constants._language_bar_a_template, entry.getKey(), entry.getValue());
				}
				if(!resultStr.isEmpty())
					resultStr += "&nbsp;&nbsp;";
			}
			return resultStr;
		}
		_log.error("Could not define locale for:" + inApplicationID);
		return ("Could not define locale for:" + inApplicationID);
	}

}
