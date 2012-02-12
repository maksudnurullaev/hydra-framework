package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

public final class Applications {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.Applications");

	static String getWhatKeyHow(
			String inWhat,
			String inKey,
			String inHow,
			IMessage inMessage) {
		
		if(inWhat.compareToIgnoreCase("All") == 0)
			return getAllKeyHow(inKey, inHow, inMessage);
		_log.error("Could not find WHAT part: " + inKey);
		return "Could not find WHAT part: " + inKey;
		
	}

	static String getAllKeyHow(
			String inKey,
			String inHow,
			IMessage inMessage) {
		
		if(inHow.compareToIgnoreCase("html") == 0)
			return getAllAnyHtml();
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
		
	}

	private static String getAllAnyHtml() {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._bean_hydra_web_applications);
		
		if(result.isOk() && result.getObject() instanceof WebApplications){
			WebApplications apps = (WebApplications) result.getObject();
			if(apps.getApplications() != null){
				StringBuffer content = new StringBuffer();
				content.append(Utils.T("template.select.apps.for.admin"));
				content.append(" <option value=''>...</option>");
				for(WebApplication app: apps.getApplications()){
					content.append(String.format("<option value='%s'>%s</option>", app.getId(), app.getId()));
				}
				content.append("</select>");
				content.append(Utils.T("template.html.hr.divId.dots",Constants._admin_app_action_div));
				return(content.toString());
			}
			_log.error("No applications!");
			return("No applications!");
		}
		_log.error("Could not find web applications!");
		return("Could not find web applications!");
	}

}
