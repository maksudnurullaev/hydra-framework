package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Utils;

public final class SystemPassword {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemLogin");

	public static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage
			) {
		
		if(inKey.compareToIgnoreCase("form") == 0)
			return getFormAny(inHow, inMessage);
		
		String tempStr = String.format("ERROR: {{System|Password|&gt;%s&lt;|%s}}",inKey, inHow);
		_log.error("Could not find KEY part for: " + tempStr);
		return tempStr ;
	}

	private static String getFormAny(
			String inHow, 
			IMessage inMessage
			) {
		String userId = Utils.getMessageDataOrNull(inMessage, Constants._userid_key);
		String locale = Utils.getMessageDataOrNull(inMessage, Constants._locale_key);
		if(userId == null || userId.isEmpty())
			return("[[Dictionary|Text|You_not_logged_in|NULL]]");
		return getPasswordChange(inHow, locale);
	}

	private static String getPasswordChange(
			String inHow, 
			String inLocale) {
		return(Utils.T("html.form.password_change"));
	}

}

