package org.hydra.utils;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.TextManager;

public final class DeployerSystem {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DeployerSystem");
	
	public static String getSystemWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow, 
			String inLocale) {
		if(inWhat.compareToIgnoreCase("LanguageBar") == 0)
			return getSystemLanguagebarKeyHow(inKey, inHow, inLocale);
		return "Could not find WHERE part: " + inWhat;
	}
	
	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow, 
			String inLocale) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inLocale);
		
		String tempStr = String.format("{{System|Languagebar|%s|%s}}",inKey, inHow);
		_log.warn("Could not find HOW part for: " + tempStr);
		return tempStr ;
	}

	private static String getSystemLanguagebarKeyA(
			String inKey, // IGNORE 
			String inLocale) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._beans_text_manager);
		if(result.isOk() && result.getObject() instanceof TextManager){ // generate language bar
			TextManager tm = (TextManager) result.getObject();
			String resultStr = "";
			for (Map.Entry<String, String> entry:tm.getLocales().entrySet()) {
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
		return "Could not find TextManager instance!";
	}



}
