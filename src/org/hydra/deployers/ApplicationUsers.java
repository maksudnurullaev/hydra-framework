package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class ApplicationUsers {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationUsers");

	static String getKeyHow(
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getKeyHtml(inKey, inLocale, inApplicationID);
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;		
	}

	static String getKeyHtml(
			String inKey, 
			String inLocale,
			String inApplicationID) {
		StringBuffer content = new StringBuffer();
		int count = DBUtils.getCountOf(inKey, "User");
		content.append("Count of users: " + (count < 0? "error" : count));		
		
		content.append(" | ");
		content.append(Utils.createJSLinkHAAD(
				Utils.Q("AdmUsers"), 
				Utils.Q("addForm"), 
				Utils.Q(inKey),
				Utils.Q("admin.app.action"), 
				"New"
				)
			);
		return content.toString();
	}

}
