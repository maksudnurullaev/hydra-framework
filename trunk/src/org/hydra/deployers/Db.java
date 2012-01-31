package org.hydra.deployers;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.CommonMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class Db {
	public static final Log _log = LogFactory.getLog("org.hydra.deployers.Db");

	public static String getWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			Map<String, String> editLinks,
			CommonMessage inMessage) {
		
		if(inWhat.compareToIgnoreCase("text") == 0){
			return getTextKeyHow(inKey, inHow, editLinks, inMessage);
		}
		
		if(inWhat.compareToIgnoreCase("template") == 0){
			return getTemplateKeyANY(inKey, inHow, editLinks, inMessage);
		}
		_log.warn(String.format("Could not find WHAT part for [[DB|->%s<-|%s|%s]]", inWhat,inKey, inHow));
		return String.format("Could not find WHAT part for {{DB|<strong>%s</strong>|%s|%s}}", inWhat,inKey, inHow) ;
	}


	private static String getTextKeyHow(
			String inKey,
			String inHow, 
			Map<String, String> editLinks,
			CommonMessage inMessage){
		String locale = inMessage.getData().get("_locale");
		if(inHow.compareToIgnoreCase("div") == 0) // div wrapper 
			return(DBUtils.wrap2IfNeeds(inMessage.getData().get("_appid"), 
					"Text", 
					inKey, 
					locale, 
					inMessage, 
					editLinks, 
					"div"));
		if(inHow.compareToIgnoreCase("span") == 0) //span wrapper
			return(DBUtils.wrap2IfNeeds(inMessage.getData().get("_appid"), 
					"Text", 
					inKey, 
					locale, 
					inMessage, 
					editLinks, 
					"span"));
		_log.warn(Utils.F("Could not find HOW part for {{DB|Text|%s|%s}}",inKey, inHow));
		return DBUtils.wrap2IfNeeds(inMessage.getData().get("_appid"), 
				"Text", 
				inKey,
				locale, 
				inMessage, 
				editLinks, 
				"span");
	};
	
	private static String getTemplateKeyANY(
			String inKey,
			String inHow,			 // reserved
			Map<String, String> editLinks,
			CommonMessage inMessage){
		return DBUtils.wrap2IfNeeds(
				inMessage.getData().get("_appid"), 
				"Template", 
				inKey, 
				"content", 
				inMessage, 
				editLinks, 
				"div");
	};
}
