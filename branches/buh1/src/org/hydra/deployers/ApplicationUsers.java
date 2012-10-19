package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DB;

public final class ApplicationUsers {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationUsers");

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
		DB.getList(inAppID, "User");
		return(null);
	}

}
