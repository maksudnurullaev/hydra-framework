package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.MessagesManager;
import org.hydra.utils.Utils;

public final class Dictionary {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.Dictionary");

	public static String getDictionaryWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID, 
			String inUserID) {
		_log.debug("WHAT: " + inWhat);
		_log.debug("KEY: " + inKey);
		_log.debug("HOW: " + inHow);			
		if(inWhat.compareToIgnoreCase("Template") == 0)
			return getDictionaryTemplateKeyANY(inKey, inHow, inLocale, inApplicationID, inUserID);
		else if(inWhat.compareToIgnoreCase("Text") == 0)
			return getDictionaryTextKeyANY(inKey, inHow, inLocale, inApplicationID, inUserID);
		
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}

	private static String getDictionaryTextKeyANY(
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID, 
			String inUserID) {
		_log.debug("KEY: " + inKey);
		_log.debug("HOW: " + inHow);		
		if(Utils.isSpecialKey(inKey)){
			if(Utils.test4Roles(inApplicationID, inUserID, "User.Administrator"))
				MessagesManager.getText(inKey, null, inLocale);
			else
				return "";
		}
		return MessagesManager.getText(inKey, null, inLocale);
	}

	private static String getDictionaryTemplateKeyANY(
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID,
			String inUserID) {
		_log.debug("KEY: " + inKey);
		_log.debug("HOW: " + inHow);			
		return MessagesManager.getTemplate(inKey);
	}

}
