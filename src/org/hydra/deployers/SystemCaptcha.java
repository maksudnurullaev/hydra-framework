package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Utils;

public final class SystemCaptcha {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemCaptcha");

	public static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage) {
		if(inKey.compareToIgnoreCase("text") == 0)
			return getTextHow(inHow, inMessage);
		
		_log.error("Could not find KEY part: " + inKey);
		return "Could not find KEY part: " + inKey;
	}

	private static String getTextHow(
			String inHow,
			IMessage inMessage) {
		if(inHow.compareToIgnoreCase("html") == 0){
			return getTextHtml(inMessage);
		}
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
	}

	private static String getTextHtml(IMessage inMessage) {
		String resultStr =  String.format("<image src=\"capcha/?appid=%s&uuid=%s\">", inMessage.getData().get("_appid"), Utils.GetUUID());
		resultStr += " = <input class=\"captcha\" id=\"" + Constants._captcha_value + "\" type=\"text\" value=\"\" size=\"3\">";
		return resultStr;
	}

}
