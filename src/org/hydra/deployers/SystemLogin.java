package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Roles;
import org.hydra.utils.Utils;

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
		if(Roles.isLocalhostAdministrator(inMessage)){
			return("[[Dictionary|Text|You_registered_as|locale]]: [[Dictionary|Text|Administrator|locale]]");
		}
		String userId = Utils.getMessageDataOrNull(inMessage, Constants._userid_key);
		String appId = Utils.getMessageDataOrNull(inMessage, Constants._appid_key);
		String locale = Utils.getMessageDataOrNull(inMessage, Constants._locale_key);
		if(userId == null || userId.isEmpty())
			return getFormLogin(inHow, locale);

		return(getUserInfo(inHow, locale, appId, userId, false));
	}

	private static String getUserInfoShort(
			String inHow, 
			IMessage inMessage,
			boolean inShort) {
		String userId = Utils.getMessageDataOrNull(inMessage, Constants._userid_key);
		String appId = Utils.getMessageDataOrNull(inMessage, Constants._appid_key);
		String locale = Utils.getMessageDataOrNull(inMessage, Constants._locale_key);

		return(getUserInfo(inHow, locale, appId, userId, inShort));
	}	
	private static String getUserInfo(
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID, 
			boolean inShort) {
		return("SystemLogin.getUserInfo()");
	}

	private static String getFormLogin(
			String inHow, 
			String inLocale) {
		return("html.form.login");
	}

}

