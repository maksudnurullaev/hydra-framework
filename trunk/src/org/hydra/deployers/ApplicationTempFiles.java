package org.hydra.deployers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.abstracts.APropertyLoader;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Roles;
import org.hydra.utils.Utils;

public class ApplicationTempFiles extends AMessageHandler {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationTemplates");

	static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage) {
		if(inKey.compareToIgnoreCase("all") == 0 && 
				inHow.compareToIgnoreCase("html") == 0)
			return getAllHtml(inMessage);
		if(inHow.compareToIgnoreCase("html") == 0)
			return getExtHtml(inKey, inMessage);
		if(inKey.compareToIgnoreCase("ExtJS") == 0 && 
				inHow.compareToIgnoreCase("UL") == 0)
			return getExtjsUl(inMessage);
		
		_log.error("Could not find KEY/HOW part: " + inKey + '/' + inHow);
		return("Could not find KEY/HOW part: " + inKey + '/' + inHow);
	}

	private static String getExtjsUl(IMessage inMessage) {
		List<String> fileURLs = new ArrayList<String>();		
		FileUtils.getListOfFiles4Dir(
				String.format(FileUtils.URL4FILES_APPID_FILES, inMessage.getData().get("_appid")),
				fileURLs,
				APropertyLoader.SUFFIX);
		if(fileURLs.isEmpty()) return("");
		boolean isAdmin = Roles.roleNotLessThen(Roles.USER_ADMINISTRATOR, inMessage);		
		int fileCount = 0;
		
		String jsActionFormat =  MessagesManager.getTemplate("template.html.a.onClick.decodeContent.Label");		 
		String encodedContent = "Application|TempFiles|%s|html";
		
		Map<String, Set<String>> filesCount = new HashMap<String, Set<String>>();
		for (String filePath : fileURLs) {
			Properties properties = FileUtils.parseProperties(filePath);
			String Public = properties.getProperty(FileUtils.FILE_DESCRIPTION_PUBLIC);
	    	boolean isPublic = ((Public != null) 
	    			&& (Public.compareToIgnoreCase("true") == 0)) ? true : false;
	    	
			if(isPublic || isAdmin){
				String file_extension = FileUtils.getFileExtension(
						FileUtils.stripPropertiesExtension(filePath));
				if(file_extension != null){
					if(!filesCount.containsKey(file_extension))
						filesCount.put(file_extension, new HashSet<String>());
					filesCount.get(file_extension).add(filePath);
					fileCount += 1;
				}
			}
	    }		
		if(fileCount > 0){
			StringBuffer content = new StringBuffer();
			content.append("<ul>");
			for(Map.Entry<String, Set<String>> entry:filesCount.entrySet()){
				content.append(Utils.F("<li>%s (%s)</li>", 
						String.format(jsActionFormat, Utils.Q(String.format(encodedContent, entry.getKey())), entry.getKey()), 
						entry.getValue().size()));
			}
			content.append(Utils.F("<li>" 
						+ String.format(jsActionFormat, Utils.Q(String.format(encodedContent, "all")), "[[DB|Text|All|locale]]")
						+ " (" + fileCount + ")</li>"));
			content.append("</ul>");
			return(content.toString());
		}	
		return("");
	}

	static String getAllHtml(IMessage inMessage) {		
		List<String> fileURLs = new ArrayList<String>();		
		FileUtils.getListOfFiles4Dir(
				String.format(FileUtils.URL4FILES_APPID_FILES, inMessage.getData().get("_appid")),
				fileURLs,
				APropertyLoader.SUFFIX);
		
		return getFilePropBox(inMessage, fileURLs);
	}	
	
	static String getExtHtml(
			String fileExtension,
			IMessage inMessage) {
		
		List<String> fileURLs = new ArrayList<String>();		
		FileUtils.getListOfFiles4Dir(
				String.format(FileUtils.URL4FILES_APPID_FILES, inMessage.getData().get("_appid")),
				fileURLs,
				(fileExtension + APropertyLoader.SUFFIX));
		
		return getFilePropBox(inMessage, fileURLs);
	}

	private static String getFilePropBox(IMessage inMessage, List<String> fileURLs) {
		StringBuffer content = new StringBuffer();
		for (String filePath : fileURLs) {
			String filebox = FileUtils.getFilePropertiesDescription(
					inMessage, 
					filePath);
			if(filebox != null) content.append(filebox);
	    }
	    if(fileURLs.size() == 0)
	    	content.append("");

		return(content.toString());
	}
}
