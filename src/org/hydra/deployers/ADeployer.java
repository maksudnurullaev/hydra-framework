package org.hydra.deployers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Utils;

public final class ADeployer {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ADeployer");
	
	public static Pattern pattern4Deployer = Pattern.compile("\\[{2}(\\S+?)\\|(\\S+?)\\|(\\S+?)\\|(\\S+?)\\]{2}");
	
	public static IMessage deployContent(
			String inContent,
			IMessage inMessage) {
		Map<String, String> editLinks = new HashMap<String, String>();
		String content = deployContent(
				inContent, 
				editLinks,
				inMessage);
		
		_log.debug("locale: " + Utils.getMessageDataOrNull(inMessage, Constants._locale_key));
		_log.debug("content size: " + content.length() + " bytes");
		
		inMessage.setHtmlContent(content);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(editLinks));
		return(inMessage);
	}

	private static String deployContent(
			String inContent, 
			Map<String, String> editLinks,
			IMessage inMessage) {
		return deployContent(inContent, 0, editLinks, inMessage);
	}	
	
	public static String deployContent(
			String inContent, 
			int recursionCount,
			Map<String, String> editLinks,
			IMessage inMessage) {
		
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
							editLinks,
							inMessage);
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
			return(deployContent(buf.toString(), recursionCount,editLinks, inMessage));
		}
		return(buf.toString());
		//return(buf.toString());
	}

	/* **** Content Deployment **** */
	private static String getWhereWhatKeyHow(
			String inWhere, 
			String inWhat,
			String inKey, 
			String inHow,
			Map<String, String> editLinks,
			IMessage inMessage) {
		if(inWhere.compareToIgnoreCase("system") == 0)
			return System.getWhatKeyHow(inWhat, inKey, inHow, inMessage);
		else if(inWhere.compareToIgnoreCase("dictionary") == 0)
			return Dictionary.getDictionaryWhatKeyHow(inWhat, inKey, inHow, inMessage);
		else if(inWhere.compareToIgnoreCase("Files") == 0)
			return Files.getWhatKeyHow(inWhat, inKey, inHow, inMessage);
		
		_log.warn("Deployer: No WHERE part: " + inWhere);
		return "Deployer: No WHERE part: " + inWhere ;
	}



}
