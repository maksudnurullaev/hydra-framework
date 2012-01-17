package org.hydra.deployers;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.Utils;

public final class Application {
	static final Log _log = LogFactory.getLog("org.hydra.deployers.Application");

	private static String getOptionsKeyHtml(
			String inKey // AppId
			) {
		return(Utils.T("Main.Adm.App.Action", inKey, inKey));				
	}

	static String getWhatKeyHow(
			String inWhat,
			String inKey, 
			String inHow, 
			String inApplicationID, 
			String inUserID) {
		if(inWhat.compareToIgnoreCase("options") == 0)
			return getOptionsKeyHow(inKey,inHow);
		else if(inWhat.compareToIgnoreCase("tags") == 0)
			return ApplicationTags.getKeyHow(inKey,inHow);
		else if(inWhat.compareToIgnoreCase("users") == 0)
			return ApplicationUsers.getKeyHow(inKey,inHow);				
		else if(inWhat.compareToIgnoreCase("templates") == 0)
			return ApplicationTemplates.getKeyHow(inKey,inHow);				
		else if(inWhat.compareToIgnoreCase("files") == 0)
			return ApplicationFiles.getKeyHow(inKey,inHow);	
		else if(inWhat.compareToIgnoreCase("ClientMessages") == 0)
			return ApplicationClientMessages.getKeyHow(inKey,inHow);	
		else if(inWhat.compareToIgnoreCase("TempFiles") == 0)
			return ApplicationTempFiles.getKeyHow(inKey,inHow, inApplicationID, inUserID);	
		
		_log.warn("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
		
	}

	private static String getOptionsKeyHow(
			String inKey, 
			String inHow) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getOptionsKeyHtml(inKey);
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
	}

}
