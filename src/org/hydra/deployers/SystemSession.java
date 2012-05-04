package org.hydra.deployers;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.SessionUtils;

public final class SystemSession {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemSession");
	

	public static String getKeyHow(String inKey, String inHow,
			IMessage inMessage) {
		if(inKey.compareToIgnoreCase("all") == 0 &&
				inHow.compareToIgnoreCase("ul") == 0)
			return getAllUl(inHow, inMessage);
		else if(inKey.compareToIgnoreCase("all") == 0 &&
				inHow.compareToIgnoreCase("pre") == 0)
			return getAllPre(inHow, inMessage);
		
		_log.error("Could not find relevant KEY/HOW part: " + inKey + '/' + inHow);
		return("Could not find relevant KEY/HOW part: " + inKey + '/' + inHow);
	}

	private static String getAllUl(String inHow, IMessage inMessage) {
		if(inMessage == null || inMessage.getSession() == null){
			return("<h3>Error: NO SESSION!</h3>");
		}
		HttpSession s = inMessage.getSession();
		StringBuffer sb = new StringBuffer();
        for (Enumeration<String> e = s.getAttributeNames() ; e.hasMoreElements() ;) {
			String key = e.nextElement();
			String value = s.getAttribute(key).toString();
			sb.append(String.format("<li><strong>%s</strong>: %s</li>", key, value));
		}
		return(String.format("<h3>Session Values</h3><ul>%s</ul>", sb.toString()));
	}	

	private static String getAllPre(String inHow, IMessage inMessage) {
		if(inMessage == null || inMessage.getSession() == null){
			return("<h3>Error: NO SESSION!</h3>");
		}
		return(String.format("<pre>%s</pre>", SessionUtils.getSessionData(inMessage)));
	}

}
