package org.hydra.utils;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DeployerDb {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DeployerDb");

	public static String getDbWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Map<String, String> editLinks) {
		_log.debug("Enter to: getDbWhatKeyHow");
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getDbTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, editLinks);
		if(inWhat.compareToIgnoreCase("template") == 0)
			return getDbTemplateKeyANY(inKey, inHow, inApplicationID, inUserID, editLinks);
		
		_log.warn(String.format("Could not find WHAT part for {{DB|%s|%s|%s}}", inWhat,inKey, inHow));
		return String.format("{{DB|%s|%s|%s}}", inWhat,inKey, inHow) ;
	}

	private static String getDbTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Map<String, String> editLinks){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("locale") == 0)
			return(DBUtils.wrap2IfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, editLinks, "div"));
		if(inHow.compareToIgnoreCase("div") == 0)
			return(DBUtils.wrap2IfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, editLinks, "div"));
		if(inHow.compareToIgnoreCase("span") == 0)
			return(DBUtils.wrap2IfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, editLinks, "span"));
		_log.error(String.format("Could not find WHAT part for {{DB|Text|%s|%s}}",inKey, inHow));
		return String.format("{{DB|Text|%s|%s}}",inKey, inHow);
	};
	
	private static String getDbTemplateKeyANY(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			Map<String, String> editLinks){
		_log.debug("Enter to: getDbTemplateKeyHow");
		return DBUtils.wrap2IfNeeds(inApplicationID, "Template", inKey, "content", inUserID, editLinks, "div");
	};
	
}
