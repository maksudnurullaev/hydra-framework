package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Roles;
import org.hydra.utils.Utils;

public final class Dictionary {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.Dictionary");

	public static String getDictionaryWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			IMessage inMessage) {
		if(inWhat.compareToIgnoreCase("Template") == 0)
			return getDictionaryTemplateKeyANY(inKey, inHow, inMessage);
		else if(inWhat.compareToIgnoreCase("Text") == 0)
			return getDictionaryTextKeyANY(inKey, inHow, inMessage);
		
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}

	private static String getDictionaryTextKeyANY(
			String inKey, 
			String inHow,
			IMessage inMessage) {
		int roleLevel = Utils.isSpecialKey(inKey);
		if(roleLevel >= 0){
			if(Roles.roleNotLessThen(roleLevel, inMessage))
				MessagesManager.getText(inKey, null, inMessage.getData().get("locale"));
			else
				return "";
		}
		return MessagesManager.getText(inKey, null, inMessage.getData().get("locale"));
	}

	private static String getDictionaryTemplateKeyANY(
			String inKey,
			String inHow,
			IMessage inMessage) {
		return MessagesManager.getTemplate(inKey);
	}

}
