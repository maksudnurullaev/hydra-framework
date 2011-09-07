package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.Utils;

public final class SystemCaptcha {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemCaptcha");

	public static String getKeyHow(
			String inKey, 
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID) {
		if(inKey.compareToIgnoreCase("text") == 0)
			return getTextHow(inHow, inLocale, inApplicationID, inUserID);
		
		_log.error("Could not find KEY part: " + inKey);
		return "Could not find KEY part: " + inKey;
	}

	private static String getTextHow(
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID) {
		if(inHow.compareToIgnoreCase("html") == 0){
			return getTextHtml(inLocale, inApplicationID, inUserID);
		}
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
	}

	private static String getTextHtml(
			String inLocale, 
			String inApplicationID,
			String inUserID) {
		String resultStr =  String.format("<image src=\"capcha/?appid=%s&uuid=%s\">", inApplicationID, Utils.GetUUID());
		resultStr += " = <input class=\"captcha\" id=\"captchaResult\" type=\"text\" value=\"\" size=\"3\">";
		return resultStr;
	}

}
