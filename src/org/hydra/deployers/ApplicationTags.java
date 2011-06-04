package org.hydra.deployers;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;

import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class ApplicationTags {

	private static String getKeyHtml(
			String inAppID,
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
		content.append(Utils.createJSLinkHAAD(
				Utils.Q("AdmTags"), 
				Utils.Q("addForm"), 
				Utils.Q(inAppID),
				Utils.Q("admin.app.action.tag"), 
				"New"
				)
			);
		Rows<String,String,String> rows = DBUtils.getRows(inAppID, "Tag", "", "", "Tag", "Tag");
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
				String jsData = Utils.getJSDataArray(
						 "handler", Utils.Q("AdmTags")
						,"action",  Utils.Q("delete")
						,"appid", Utils.Q(inAppID)
						,"value", Utils.Q(key)
						,"dest", Utils.Q("admin.app.action.tag")
					);			
				content2.append(Utils.createJSLinkWithConfirm(jsData, "Delete"));			
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
			String inKey, 
			String inHow,
			String inLocale, 
			String inApplicationID) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getKeyHtml(inKey, inLocale, inApplicationID);
	
		
		Application._log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;
	}

}
