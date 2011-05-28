package org.hydra.deployers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Moder;


public final class Db {
	public static final Log _log = LogFactory.getLog("org.hydra.deployers.Db");

	public static String getWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links) {
		_log.debug("Enter to: getDbWhatKeyHow");
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, inModer, links);
		if(inWhat.compareToIgnoreCase("template") == 0)
			return getTemplateKeyANY(inKey, inHow, inApplicationID, inUserID, inModer, links);
		
		_log.warn(String.format("Could not find WHAT part for {{DB|%s|%s|%s}}", inWhat,inKey, inHow));
		return String.format("{{DB|%s|%s|%s}}", inWhat,inKey, inHow) ;
	}

	private static String getTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("locale") == 0)
			return getTextKeyLocale(inKey, inApplicationID, inLocale, inUserID, inModer, links);
		_log.error(String.format("Could not find WHAT part for {{DB|Text|%s|%s}}",inKey, inHow));
		return String.format("{{DB|Text|%s|%s}}",inKey, inHow);
	};
	
	private static String getTemplateKeyANY(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			Moder inModer,           // reserved
			List<String> links){
		_log.debug("Enter to: getDbTemplateKeyHow");
		return DBUtils.wrap2DivIfNeeds(inApplicationID, "Template", inKey, "html", inUserID, inModer, links);
	};

	private static String getTextKeyLocale(
			String inKey,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links) {
		_log.debug("Enter to: getDbTextKeyLocale");		
		return DBUtils.wrap2DivIfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, inModer, links);
	};
}
