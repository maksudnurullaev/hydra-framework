package org.hydra.deployers;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.Utils;

public final class Application {
	static final Log _log = LogFactory.getLog("org.hydra.deployers.Application");

	private static String getOptionsKeyHtml(
			String inKey, // AppId
			String inLocale, 
			String inApplicationID) {
		
		StringBuffer content = new StringBuffer(inKey + ": ");
		
		content.append(Utils.createJSLinkHAKD(
				Utils.Q("AdmTags"), 
				Utils.Q("getTagsFor"), 
				Utils.Q(inKey), 
				Utils.Q("admin.app.action"), 
				"Tags"
				));
		content.append(" | ");
		content.append(Utils.createJSLinkHAKD(
				Utils.Q("AdmUsers"), 
				Utils.Q("getUsersFor"), 
				Utils.Q(inKey), 
				Utils.Q("admin.app.action"), 
				"Users"));	
		
		content.append(Utils.T("template.html.hr.divId.dots","admin.app.action"));
		return(content.toString());
	}

	static String getWhatKeyHow(
			String inWhat,
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		if(inWhat.compareToIgnoreCase("options") == 0)
			return getOptionsKeyHow(inKey,inHow, inLocale, inApplicationID);
		else if(inWhat.compareToIgnoreCase("tags") == 0)
			return ApplicationTags.getKeyHow(inKey,inHow, inLocale, inApplicationID);
		else if(inWhat.compareToIgnoreCase("users") == 0)
			return ApplicationUsers.getKeyHow(inKey,inHow, inLocale, inApplicationID);				
		
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
		
	}

	private static String getOptionsKeyHow(
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getOptionsKeyHtml(inKey, inLocale, inApplicationID);
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
	}

}
