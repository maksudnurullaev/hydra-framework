package org.hydra.deployers;

import java.util.Iterator;
import java.util.Map;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.beans.WebApplications;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

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
		else if(inWhat.compareToIgnoreCase("Application") == 0)
			return getSystemApplicatonKeyHow(inKey, inHow, inLocale, inApplicationID);
		
		_log.error("Could not find WHAT part: " + inWhat);
		return "Could not find WHAT part: " + inWhat;
	}
	
	private static String getSystemApplicatonKeyHow(
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		
		if(inHow.compareToIgnoreCase("html") == 0)
			return getSystemApplicationKeyHtml(inKey, inLocale, inApplicationID);
		else if(inHow.compareToIgnoreCase("users") == 0)
			return getSystemApplicationKeyUsers
			(inKey, inLocale, inApplicationID);
		else if(inHow.compareToIgnoreCase("tags") == 0)
			return getSystemApplicationKeyTags
			(inKey, inLocale, inApplicationID);		
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
		
	}

	private static String getSystemApplicationKeyTags(
			String inKey,
			String inLocale, 
			String inApplicationID) {
		StringBuffer content = new StringBuffer();
		content.append("<strong>Global tags</strong><hr />");
		for(String tag:Constants._GLOBAL_TAGS){
			content.append(Utils.T("template.html.divId.Content", tag, tag));			
		}
		content.append("<hr />");
		content.append("<div id='admin.app.action.tag'>");
		content.append("<strong>Dinamic tags</strong> | ");
		content.append(Utils.createJSLinkHAKD(
				Utils.Q("AdmTags"), 
				Utils.Q("newTagForm"), 
				Utils.Q(inKey),
				Utils.Q("admin.app.action.tag"), 
				"New"
				)
			);
		Rows<String,String,String> rows = DBUtils.getRows(inKey, "Tag", "Tag", "Tag");
		int validTags = 0;
        for (Row<String, String, String> r : rows) {
        	String key = r.getKey();
            _log.debug(" key:" + key);
            HColumn<String, String> colResult = 
            	DBUtils.getColumn(inKey, "Tag", r.getKey(), "name");
            if(colResult != null){
            	String value = colResult.getValue();
            	_log.debug(" value: " + value);
    			StringBuffer content2 = new StringBuffer();
    			// edit link
    			String jsData = Utils.getJSDataArray(
    						 "handler", Utils.Q("AdmTags")
    						,"action",  Utils.Q("editTag")
    						,"key", Utils.Q(inKey)
    						,"value", Utils.Q(key)
    						,"dest", Utils.Q("admin.app.action.tag")
    					);
    			content2.append(Utils.createJSLink(jsData, "Edit"));
    			// delete link
    			content2.append("&nbsp;");
    			jsData = Utils.getJSDataArray(
    					 "handler", Utils.Q("AdmTags")
    					,"action",  Utils.Q("deleteTag")
    					,"key", Utils.Q(inKey)
    					,"value", Utils.Q(key)
    					,"dest", Utils.Q("admin.app.action.tag")
    				);			
    			content2.append(Utils.createJSLinkWithConfirm(jsData, "Delete"));			
    			// value
    			content2.append("&nbsp;");
    			content2.append(value);
    			content.append(Utils.T("template.html.divId.Content",key,content2.toString()));     
    			validTags++;
            }
        }
        if(validTags == 0)
        	content.append("<div>...</div>");
		content.append("</div>");
		return content.toString();
		
	}

	private static String getSystemApplicationKeyUsers(
			String inKey,
			String inLocale, 
			String inApplicationID) {
		
		StringBuffer result = new StringBuffer();
		int count = DBUtils.getCountOf(inKey, "User");
		result.append("Count of users: " + (count < 0? "error" : count));		
		// TODO Not finished yet!!!
		return result.toString();
	}

	private static String getSystemApplicationKeyHtml(
			String inKey, // AppId
			String inLocale, 
			String inApplicationID) {
		
		StringBuffer content = new StringBuffer(inKey + ": ");
		
		content.append(Utils.createJSLinkHAKD(
				Utils.Q("AdmUsers"), 
				Utils.Q("getUsersFor"), 
				Utils.Q(inKey), 
				Utils.Q("admin.app.action"), 
				"Users")
			);		
		content.append(" | ");
		content.append(Utils.createJSLinkHAKD(
				Utils.Q("AdmTags"), 
				Utils.Q("getTagsFor"), 
				Utils.Q(inKey), 
				Utils.Q("admin.app.action"), 
				"Tags"
				)
			);	
		
		content.append(Utils.T("template.html.hr.divId.dots","admin.app.action"));
		return(content.toString());
	}
	
	private static String getSystemApplicationsKeyHow(
			String inKey,
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		
		if(inKey.compareToIgnoreCase("All") == 0)
			return getSystemApplicationsAllHow(inHow, inLocale, inApplicationID);
		_log.error("Could not find KEY part: " + inKey);
		return "Could not find KEY part: " + inKey;
		
	}

	private static String getSystemApplicationsAllHow(
			String inHow,
			String inLocale, 
			String inApplicationID) {
		
		if(inHow.compareToIgnoreCase("html") == 0)
			return getSystemApplicationsAllHtml(inLocale, inApplicationID);
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
		
	}

	private static String getSystemApplicationsAllHtml(
			String inLocale,
			String inApplicationID) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._bean_hydra_web_applications);
		
		if(result.isOk() && result.getObject() instanceof WebApplications){
			WebApplications apps = (WebApplications) result.getObject();
			if(apps.getApplications() != null){
				StringBuffer content = new StringBuffer();
				content.append("Application: ");
				content.append("<select id=\"admin.select.applications\" onchange=\"javascript:void(Globals.sendMessage({handler: 'Adm',action: 'getApp', key:this.value, dest: 'admin.app'})); return false;\">");
				content.append(" <option value=''>Select...</option>");
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

	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow, 
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inLocale, inApplicationID);
		
		String tempStr = String.format("{{System|Languagebar|%s|%s}}",inKey, inHow);
		_log.error("Could not find HOW part for: " + tempStr);
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
		_log.error("Could not define locale for:" + inApplicationID);
		return ("Could not define locale for:" + inApplicationID);
	}

}
