package org.hydra.deployers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;

public class ApplicationImages {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationImages");

	public static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage) {
		if(inHow.compareToIgnoreCase("jsibox_col4") == 0)
			return getFoldersLightbox(inKey, inMessage);
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;		
	}

	private static String getFoldersLightbox(
			String inFolders, 
			IMessage inMessage) {
		String appid = inMessage.getData().get("_appid");
		_log.debug("Images as lightbox: ");
		_log.debug("App id: " + appid);
		_log.debug("Folder: " + inFolders);
		
		StringBuffer content = new StringBuffer("<div id='gallery'>");
		
		List<String> fileURLs = new ArrayList<String>();		
		FileUtils.getListOfFiles4Dir(
				String.format(FileUtils.URL4FILES_APPID_SUBFOLDER, appid, inFolders),
				fileURLs,
				null);		
		
		String format = "<a href='%s' rel='rr' onclick='return jsiBoxOpen(this)' title='%s'><img src='%s' height='150' alt='sample image' /></a> ";
		
		for (String filePath : fileURLs) {
			content.append(String.format(format, filePath, filePath, filePath));
	    }
		
	    if(fileURLs.size() == 0)
	    	content.append("...");
		content.append("</div>");
		
		return(content.toString());
	}

}
