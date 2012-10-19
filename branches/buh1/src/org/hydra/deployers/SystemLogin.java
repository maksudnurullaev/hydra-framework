package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

import org.apache.commons.lang.NotImplementedException;

public final class SystemLogin {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemLogin");

	public static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage
			) {
		
		if(inKey.compareToIgnoreCase("form") == 0)
			return getFormAny(inHow, inMessage);
		else if(inKey.compareToIgnoreCase("Info") == 0 &&
				inHow.compareToIgnoreCase("short") == 0)
			return getUserInfoShort(inHow, inMessage, true);
		
		String tempStr = String.format("ERROR: {{System|Login|&gt;%s&lt;|&gt;%s&lt;}}",inKey, inHow);
		_log.error("Could not find KEY part for: " + tempStr);
		return tempStr ;
	}

	private static String getFormAny(
			String inHow, 
			IMessage inMessage) {
		String userId = inMessage.getData().get("_userid");
		String appId = inMessage.getData().get("appid");
		String locale = inMessage.getData().get("locale");
		if(userId == null || userId.isEmpty())
			return getFormLogin(inHow, locale);

		return(getUserInfo(inHow, locale, appId, userId, false));
	}

	private static String getUserInfoShort(
			String inHow, 
			IMessage inMessage,
			boolean inShort) {
		String userId = inMessage.getData().get("_userid");
		String appId = inMessage.getData().get("appid");
		String locale = inMessage.getData().get("locale");

		return(getUserInfo(inHow, locale, appId, userId, inShort));
	}	
	private static String getUserInfo(
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID, 
			boolean inShort) {
		throw new NotImplementedException();
	}

	private static String getFormLogin(
			String inHow, 
			String inLocale) {
		return(Utils.T("html.form.login"));
	}

}

