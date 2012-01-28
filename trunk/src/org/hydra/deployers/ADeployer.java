package org.hydra.deployers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

public final class ADeployer {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ADeployer");
	
	public static Pattern pattern4Deployer = Pattern.compile("\\[{2}(\\S+?)\\|(\\S+?)\\|(\\S+?)\\|(\\S+?)\\]{2}");
	
	public static IMessage deployContent(
			String inContent,
			CommonMessage inMessage) {
		deployContent2(inContent, inMessage);
		return(inMessage);
	}

	public static String deployContent2(
			String inContent,
			CommonMessage inMessage) {
		Map<String, String> editLinks = new HashMap<String, String>();
		String content = deployContent(
				inContent, 
				inMessage.getData().get("appid"), 
				inMessage.getLocale(), 
				inMessage.getUserId(),
				editLinks);
				
		inMessage.setHtmlContent(content);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(editLinks));
		
		return(content);
	}	
	
	public static String deployContent(
			String inContent, 
			String inApplicationID, 
			String inLocale, 
			String inUserID, 
			int recursionCount,
			Map<String, String> editLinks) {
		
		if(++recursionCount > 10){
			_log.warn("No more recursion permited!!!");
			return inContent;
		}
		
		Matcher matcher = pattern4Deployer.matcher(inContent);
	
		StringBuffer buf = new StringBuffer();
		while ((matcher.find())) {
			_log.debug("WHERE: " + matcher.group(1));
			_log.debug("FROM: " + matcher.group(2));
			_log.debug("KEY: " + matcher.group(3));
			_log.debug("HOW: " + matcher.group(4));	
			String tempContent 
				= getWhereWhatKeyHow(
							matcher.group(1),  // WHERE
							matcher.group(2),  // WHAT
							matcher.group(3),  // KEY
							matcher.group(4),  // HOW
							inApplicationID, 
							inLocale,
							inUserID,
							editLinks);
			//tempContent.replace("$", "\\$");
			matcher.appendReplacement(
					buf,
					tempContent.replace("$", "\\$")				
					);
		}
		matcher.appendTail(buf);
		// finish
		matcher = pattern4Deployer.matcher(buf.toString());
		if(matcher.find()){
			_log.debug("Found recursive entring, recursionCount: " + recursionCount);
			return(deployContent(buf.toString(), inApplicationID, inLocale, inUserID, recursionCount,editLinks));
		}
		return(buf.toString());
		//return(buf.toString());
	}

	/* **** Content Deployment **** */
	private static String deployContent(
			String inContent, 
			String inApplicationID, 
			String inLocale, 
			String inUserID, 
			Map<String, String> editLinks) {
		_log.debug("ApplicationID: " + inApplicationID);
		_log.debug("Locale: " + inLocale);
		_log.debug("UserID: " + inUserID);
		return deployContent(inContent, inApplicationID, inLocale, inUserID, 0, editLinks);
	}

	private static String getWhereWhatKeyHow(
			String inWhere, 
			String inWhat,
			String inKey, 
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Map<String, String> editLinks) {
		if(inWhere.compareToIgnoreCase("db") == 0)
			return Db.getWhatKeyHow(inWhat, inKey, inHow, inApplicationID, inLocale, inUserID, editLinks);
		else if(inWhere.compareToIgnoreCase("system") == 0)
			return System.getWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID, inUserID);
		else if(inWhere.compareToIgnoreCase("dictionary") == 0)
			return Dictionary.getDictionaryWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID, inUserID);
		else if(inWhere.compareToIgnoreCase("Applications") == 0)
			return Applications.getWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID);
		else if(inWhere.compareToIgnoreCase("Application") == 0)
			return Application.getWhatKeyHow(inWhat, inKey, inHow, inApplicationID, inUserID);
		
		return "Deployer: No WHERE part: " + inWhere ;
	}



}
