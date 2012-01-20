package org.hydra.deployers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class Db {
	public static final Log _log = LogFactory.getLog("org.hydra.deployers.Db");

	public static String getWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Map<String, String> editLinks) {
		_log.debug("Enter to: getDbWhatKeyHow");
		
		if(inWhat.compareToIgnoreCase("text") == 0){
			return getTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, editLinks);
		}
		if(inWhat.compareToIgnoreCase("NonUserText") == 0){
			if(inUserID == null)
				return getTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, editLinks);
			else
				return "&nbsp;";
		}
		if(inWhat.compareToIgnoreCase("template") == 0){
			return getTemplateKeyANY(inKey, inHow, inApplicationID, inUserID, editLinks);
		}
		/*
		if(inWhat.compareToIgnoreCase("NonUserTemplate") == 0){
			if(inUserID == null)
				return getTemplateKeyANY(inKey, inHow, inApplicationID, inUserID, editLinks);
			else
				return "&nbsp;";
		}
		*/
		_log.warn(String.format("Could not find WHAT part for [[DB|->%s<-|%s|%s]]", inWhat,inKey, inHow));
		return String.format("Could not find WHAT part for {{DB|<strong>%s</strong>|%s|%s}}", inWhat,inKey, inHow) ;
	}


	private static String getTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Map<String, String> editLinks){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("div") == 0) // div wrapper 
			return(DBUtils.wrap2IfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, editLinks, "div"));
		if(inHow.compareToIgnoreCase("span") == 0) //span wrapper
			return(DBUtils.wrap2IfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, editLinks, "span"));
		_log.warn(Utils.F("Could not find HOW part for {{DB|Text|%s|%s}}",inKey, inHow));
		return DBUtils.wrap2IfNeeds(inApplicationID, "Text", inKey, inLocale, inUserID, editLinks, "span");
	};
	
	private static String getTemplateKeyANY(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			Map<String, String> editLinks){
		_log.debug("Try to insert...");
		_log.debug("inKeyspace: " + inApplicationID);
		_log.debug("inColumnFamily: " + "Template");
		_log.debug("inKey: " + inKey);
		return DBUtils.wrap2IfNeeds(inApplicationID, "Template", inKey, "content", inUserID, editLinks, "div");
	};
}
