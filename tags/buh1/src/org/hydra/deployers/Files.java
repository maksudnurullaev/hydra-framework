package org.hydra.deployers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.SessionUtils;

public final class Files {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.Files");
	public static String getWhatKeyHow(
			String inWhat, // what 
			String inKey,  // elementId
			String inHow,  // prefix
			IMessage inMessage) {
		if(inWhat.compareToIgnoreCase("html") == 0 
				&& inHow.compareToIgnoreCase("by_role") == 0)
			return getHtmlByRole(inKey, inMessage);
		if(inWhat.compareToIgnoreCase("html") == 0 
				&& inHow.compareToIgnoreCase("none") == 0)
			return getHtmlNone(inKey, inMessage);
		_log.error("Could not find WHAT(and/or HOW) part: " + inWhat + " (and/or " + inHow + ")");
		return "Could not find WHAT part: " + inWhat;		
	}

	private static String getHtmlByRole(String inKey, IMessage inMessage) {
		List<String> roles = SessionUtils.getSessionRoles(inMessage);
		StringBuffer sb = new StringBuffer();
		String appId = inMessage.getData().get("appid");
		for(String role:roles)
			sb.append(FileUtils.getHtmlFromFile(appId, String.format("%s.%s", inKey, role)));
		return(sb.toString());
	}

	private static String getHtmlNone(String inKey, IMessage inMessage) {
		String appId = inMessage.getData().get("appid");
		StringBuffer sb = new StringBuffer();
		sb.append(FileUtils.getHtmlFromFile(appId, inKey));
		return(sb.toString());
	}
}
