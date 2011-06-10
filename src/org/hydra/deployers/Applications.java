package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
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
			String inLocale, 
			String inApplicationID) {
		
		if(inWhat.compareToIgnoreCase("All") == 0)
			return getAllKeyHow(inKey, inHow, inLocale, inApplicationID);
		_log.error("Could not find WHAT part: " + inKey);
		return "Could not find WHAT part: " + inKey;
		
	}

	static String getAllKeyHow(
			String inKey,
			String inHow,
			String inLocale, 
			String inApplicationID) {
		
		if(inHow.compareToIgnoreCase("html") == 0)
			return getAllAnyHtml(inLocale, inApplicationID);
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
		
	}

	private static String getAllAnyHtml(
			String inLocale,
			String inApplicationID) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._bean_hydra_web_applications);
		
		if(result.isOk() && result.getObject() instanceof WebApplications){
			WebApplications apps = (WebApplications) result.getObject();
			if(apps.getApplications() != null){
				StringBuffer content = new StringBuffer();
				content.append("<select onchange=\"javascript:void(Globals.sendMessage({handler: 'Adm',action: 'getApp', appid:this.value, dest: 'admin.app'})); return false;\">");
				content.append(" <option value=''>...</option>");
				for(WebApplication app: apps.getApplications()){
					content.append(String.format("<option value='%s'>%s</option>", app.getId(), app.getId()));
				}
				content.append("</select>");
				content.append(Utils.T("template.html.hr.divId.dots","admin.app"));
				return(content.toString());
			}
			_log.error("No applications!");
			return("No applications!");
		}
		_log.error("Could not find web applications!");
		return("Could not find web applications!");
	}

}
