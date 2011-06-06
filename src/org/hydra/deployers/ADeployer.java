package org.hydra.deployers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Moder;
import org.hydra.utils.Utils;

public final class ADeployer {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ADeployer");
	
	public static Pattern pattern4Deployer = Pattern.compile("\\[\\[(\\S+)\\|(\\S+)\\|(\\S+)\\|(\\S+)\\]\\]");
	
	public static IMessage deployContent(
			String inContent,
			CommonMessage inMessage) {
		List<String> links = new ArrayList<String>();
		String content = deployContent(
				inContent, 
				inMessage._web_application.getId(), 
				inMessage._locale, 
				inMessage._user_id,
				inMessage._moder,
				links);
				
		inMessage.setHtmlContent(content);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
		
		return(inMessage);
	}

	public static String deployContent(
			String inContent, 
			String inApplicationID, 
			String inLocale, 
			String inUserID, 
			Moder inModer,
			int recursionCount,
			List<String> links) {
		
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
							inModer,
							links);
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
			return(deployContent(buf.toString(), inApplicationID, inLocale, inUserID, inModer,recursionCount,links));
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
			Moder inModer,
			List<String> links) {
		_log.debug("ApplicationID: " + inApplicationID);
		_log.debug("Locale: " + inLocale);
		_log.debug("UserID: " + inUserID);
		_log.debug("Mode: " + inModer.getMode());
		return deployContent(inContent, inApplicationID, inLocale, inUserID, inModer, 0, links);
	}

	private static String getWhereWhatKeyHow(
			String inWhere, 
			String inWhat,
			String inKey, 
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links) {
		if(inWhere.compareToIgnoreCase("db") == 0)
			return Db.getWhatKeyHow(inWhat, inKey, inHow, inApplicationID, inLocale, inUserID, inModer, links);
		else if(inWhere.compareToIgnoreCase("system") == 0)
			return System.getWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID, inUserID);
		else if(inWhere.compareToIgnoreCase("dictionary") == 0)
			return Dictionary.getDictionaryWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID);
		else if(inWhere.compareToIgnoreCase("Applications") == 0)
			return Applications.getWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID);
		else if(inWhere.compareToIgnoreCase("Application") == 0)
			return Application.getWhatKeyHow(inWhat, inKey, inHow, inLocale, inApplicationID);
		
		return "Deployer: No WHERE part: " + inWhere ;
	}



}
