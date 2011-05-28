package org.hydra.utils;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.managers.MessagesManager;

public final class DeployerSystem {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DeployerSystem");
	
	public static String getSystemWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		if(inWhat.compareToIgnoreCase("LanguageBar") == 0)
			return getSystemLanguagebarKeyHow(inKey, inHow, inLocale, inApplicationID);
		else if(inWhat.compareToIgnoreCase("Applications") == 0)
			return getSystemApplicationsKeyHow(inKey, inHow, inLocale, inApplicationID);
		else if(inWhat.compareToIgnoreCase("Users") == 0)
			return getSystemUsersAppHow(inKey, inHow, inLocale, inApplicationID);
		return "Could not find WHAT part: " + inWhat;
	}
	
	private static String getSystemUsersAppHow(
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("Options") == 0)
			return getSystemUsersAppOptions(inKey, inLocale, inApplicationID);
		return "Could not find HOW part: " + inHow;
	}

	private static String getSystemUsersAppOptions(
			String inKey, // AppId
			String inLocale, 
			String inApplicationID) {		
		return("NOT_IMPLEMENTED_YET: getSystemUsersAppOptions:" + inKey);		
	}

	private static String getSystemApplicationsKeyHow(
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		if(inKey.compareToIgnoreCase("All") == 0)
			return getSystemApplicationsAllHow(inHow, inLocale, inApplicationID);
		return "Could not find KEY part: " + inKey;
	}

	private static String getSystemApplicationsAllHow(
			String inHow,
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("Options") == 0)
			return getSystemApplicationsAllOptions(inLocale, inApplicationID);
		return "Could not find HOW part: " + inHow;
	}

	private static String getSystemApplicationsAllOptions(
			String inLocale,
			String inApplicationID) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._bean_hydra_web_applications);
		
		if(result.isOk() && result.getObject() instanceof WebApplications){
			WebApplications apps = (WebApplications) result.getObject();
			if(apps.getApplications() != null){
				StringBuffer content = new StringBuffer();
				content.append("Application: ");
				content.append("<select id=\"admin.select.applications\" onchange=\"javascript:void(Globals.sendMessage({handler: 'AdmUsers',action: 'getUsers4', appid:this.value, dest: 'admin.users'})); return false;\">");
				content.append(" <option value=''>Select...</option>");
				for(WebApplication app: apps.getApplications()){
					content.append(String.format("<option value='%s'>%s</option>", app.getId(), app.getId()));
				}
				content.append("</select>");
				
				content.append(Utils.T("template.html.hr.divId.dots","admin.users"));
				
				return(content.toString());
			}
			return("No applications!");
		}
		return("Could not find web applications!");
	}

	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inLocale, inApplicationID);
		
		String tempStr = String.format("{{System|Languagebar|%s|%s}}",inKey, inHow);
		_log.warn("Could not find HOW part for: " + tempStr);
		return tempStr ;
	}

	private static String getSystemLanguagebarKeyA(
			String inKey, // IGNORE 
			String inLocale, 
			String inApplicationID) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, (inApplicationID + Constants._bean_web_app_id_postfix));
		if(result.isOk() && result.getObject() instanceof WebApplication){ // generate language bar
			WebApplication app = (WebApplication) result.getObject();
			String resultStr = "";
			for (Map.Entry<String, String> entry:app.getLocales().entrySet()) {
				if(entry.getKey().compareToIgnoreCase(inLocale) == 0){ // selected
					resultStr += entry.getValue();
				}else{
					resultStr += String.format(Constants._language_bar_a_template, entry.getKey(), entry.getValue());
				}
				if(!resultStr.isEmpty())
					resultStr += "&nbsp;&nbsp;";
			}
			return resultStr;
		}
		return ("Could not define locale for:" + inApplicationID);
	}



}
