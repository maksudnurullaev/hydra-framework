package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.MessagesManager;

public final class DeployerDictionary {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.DeployerDictionary");

	public static String getDictionaryWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		_log.debug("WHAT: " + inWhat);
		_log.debug("KEY: " + inKey);
		_log.debug("HOW: " + inHow);			
		if(inWhat.compareToIgnoreCase("Template") == 0)
			return getDictionaryTemplateKeyANY(inKey, inHow, inLocale, inApplicationID);
		else if(inWhat.compareToIgnoreCase("Text") == 0)
			return getDictionaryTextKeyANY(inKey, inHow, inLocale, inApplicationID);
		
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}

	private static String getDictionaryTextKeyANY(
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		_log.debug("KEY: " + inKey);
		_log.debug("HOW: " + inHow);			
		return MessagesManager.getText(inKey, null, inLocale);
	}

	private static String getDictionaryTemplateKeyANY(
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		_log.debug("KEY: " + inKey);
		_log.debug("HOW: " + inHow);			
		return MessagesManager.getTemplate(inKey);
	}

}
