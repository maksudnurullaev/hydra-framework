package org.hydra.deployers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Utils;

public class ApplicationFiles extends AMessageHandler {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationTemplates");

	static String getKeyHow(
			String inKey, 
			String inHow) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getANYHtml(inKey);
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;		
	}

	static String getANYHtml(
			String inAppID) {
		StringBuffer content = new StringBuffer();
		
		List<String> fileURLs = new ArrayList<String>();		
		FileUtils.getListOfFiles4Dir(
				String.format(FileUtils.URL4FILES_APPID_IMAGE, inAppID),
				fileURLs,
				null);		
		
		for (String filePath : fileURLs) {
			content.append(FileUtils.getFileBox(inAppID, filePath));
	    }
	    if(fileURLs.size() == 0)
	    	content.append("...");
    	StringBuffer header = new StringBuffer();
    	header.append("Count: " + fileURLs.size());					
	    header.append(" | ");
	    header.append(Utils.createJSLinkAdmNewFile(inAppID,"admin.app.action"));
			
	    header.append("<hr />");

		return(header.toString() + content.toString());
	}	
	
	
}
