package org.hydra.utils;

import java.util.List;
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
			List<String> links) {
		_log.debug("Enter to: getDbWhatKeyHow");
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getDbTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, links);
		if(inWhat.compareToIgnoreCase("template") == 0)
			return getDbTemplateKeyANY(inKey, inHow, inApplicationID, inUserID, links);
		
		_log.warn(String.format("Could not find WHAT part for {{DB|%s|%s|%s}}", inWhat,inKey, inHow));
		return String.format("{{DB|%s|%s|%s}}", inWhat,inKey, inHow) ;
	}

	private static String getDbTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			List<String> links){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("locale") == 0)
			return getDbTextKeyLocale(inKey, inApplicationID, inLocale, inUserID, links);
		_log.error(String.format("Could not find WHAT part for {{DB|Text|%s|%s}}",inKey, inHow));
		return String.format("{{DB|Text|%s|%s}}",inKey, inHow);
	};
	
	private static String getDbTemplateKeyANY(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			List<String> links){
		_log.debug("Enter to: getDbTemplateKeyHow");
		return DBUtils.wrap2DivIfNeeds(inApplicationID, "Template", inKey, "content", inUserID, links);
	};
	
	private static String getDbTextKeyLocale(
			String inKey,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			List<String> links) {
		_log.debug("Enter to: getDbTextKeyLocale");		
		return(DBUtils.wrap2DivIfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, links));
	};

}
