package org.hydra.deployers;

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
			return getKeyHtml(inKey);
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;		
	}

	static String getKeyHtml(
			String inAppID) {
		StringBuffer content = new StringBuffer();
		
		List<String> filePathes = FileUtils.getListOfFiles(inAppID);
		_log.debug("filePathes.size(): " + filePathes.size());
		for (String filePath : filePathes) {
			content.append(FileUtils.getFileBox(inAppID, filePath));
	    }
	    if(filePathes.size() == 0)
	    	content.append("...");
    	StringBuffer header = new StringBuffer();
    	header.append("Count: " + filePathes.size());					
	    header.append(" | ");
	    header.append(Utils.createJSLinkHAAD(
					Utils.Q("AdmFiles"), 
					Utils.Q("addForm"), 
					Utils.Q(inAppID),
					Utils.Q("admin.app.action"), 
					"New"
					)
	    		);
			
	    header.append("<hr />");

		return(header.toString() + content.toString());
	}	
}
