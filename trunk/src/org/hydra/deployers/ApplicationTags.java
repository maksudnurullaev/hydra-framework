package org.hydra.deployers;

import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;

import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class ApplicationTags {

	private static String getKeyHtml(
			String inAppID) {
		StringBuffer content = new StringBuffer();
		content.append("<strong>Global tags</strong><hr />");
		for(String tag:Constants._GLOBAL_TAGS){
			content.append(Utils.T("template.html.divId.Content", tag, tag));			
		}
		content.append("<hr />");
		content.append("<div id='admin_app_action_tag'>");
		content.append("<strong>Other tags</strong> | ");
		content.append(Utils.createJSLinkHAAD(
				Utils.Q("AdmTags"), 
				Utils.Q("addForm"), 
				Utils.Q(inAppID),
				Utils.Q("admin_app_action_tag"), 
				"New"
				)
			);
		List<Row<String,String,String>> rows = DBUtils.getValidRows(inAppID, "Tag", "", "", "", "");
		int validRows = 0;
	    for (Row<String, String, String> r : rows) {
	    	String key = r.getKey();
	        Application._log.debug(" key:" + key);
	        HColumn<String, String> colResult = 
	        	DBUtils.getColumn(inAppID, "Tag", r.getKey(), "name");
	        if(colResult != null){
	        	String value = colResult.getValue();
	        	Application._log.debug(" value: " + value);
				StringBuffer content2 = new StringBuffer();
				// delete link
				content2.append("&nbsp;");
				String jsData = Utils.jsData(
						 "handler", Utils.Q("AdmTags")
						,"action",  Utils.Q("delete")
						,"appid", Utils.Q(inAppID)
						,"value", Utils.Q(key)
						,"dest", Utils.Q("admin_app_action_tag")
					);			
				content2.append(Utils.F("[%s]", Utils.createJSLinkWithConfirm("Delete", jsData, "X")));			
				// value
				content2.append("&nbsp;");
				content2.append(value);
				content.append(Utils.T("template.html.divId.Content",key,content2.toString()));     
				validRows++;
	        }
	    }
	    if(validRows == 0)
	    	content.append("<div>...</div>");
		content.append("</div>");
		
		return content.toString();
		
	}

	static String getKeyHow(
			String inKey, // AppID
			String inHow) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getKeyHtml(inKey);
	
		
		Application._log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
	}

}
