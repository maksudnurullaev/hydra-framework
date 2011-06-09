package org.hydra.deployers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils;

public final class Db {
	public static final Log _log = LogFactory.getLog("org.hydra.deployers.Db");

	public static String getWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			List<String> links) {
		_log.debug("Enter to: getDbWhatKeyHow");
		
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, links);
		if(inWhat.compareToIgnoreCase("template") == 0)
			return getTemplateKeyANY(inKey, inHow, inApplicationID, inUserID, links);
		
		_log.warn(String.format("Could not find WHAT part for {{DB|!!!%s!!!|%s|%s}}", inWhat,inKey, inHow));
		return String.format("Could not find WHAT part for {{DB|!!!%s!!!|%s|%s}}", inWhat,inKey, inHow) ;
	}

	private static String getTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			List<String> links){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("locale") == 0)
			return getTextKeyLocale(inKey, inApplicationID, inLocale, inUserID, links);
		_log.error(String.format("Could not find Key part for {{DB|Text|%s|%s}}",inKey, inHow));
		return DBUtils.wrap2DivIfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, links);
	};
	
	private static String getTemplateKeyANY(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			List<String> links){
		_log.debug("Try to insert...");
		_log.debug("inKeyspace: " + inApplicationID);
		_log.debug("inColumnFamily: " + "Template");
		_log.debug("inKey: " + inKey);
		return DBUtils.wrap2DivIfNeeds(inApplicationID, "Template", inKey, "content", inUserID, links);
	};

	private static String getTextKeyLocale(
			String inKey,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			List<String> links) {
		_log.debug("Enter to: getDbTextKeyLocale");		
		return DBUtils.wrap2DivIfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, links);
	};
}
