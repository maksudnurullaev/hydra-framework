package org.hydra.deployers;

import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;

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
			String inAppID, 
			String inLocale,
			String inApplicationID) {
		StringBuffer content = new StringBuffer();
		
		List<Row<String,String,String>> rows = DBUtils.getValidRows(inAppID, "User", "", "", "", "");
		int validRows = 0;
		
	    for (Row<String, String, String> r : rows) {
	    	HColumn<String, String> colPassword = r.getColumnSlice().getColumnByName("password");
	    	if(colPassword != null){
	        	validRows++;
        		content.append("<div style=\"margin: 5px; padding: 5px; border: 1px solid rgb(127, 157, 185);\">");
	        	
	        	// ... delete link
	        	content.append(getDeleteLink(inAppID, r.getKey()) + " ");
	        	// ... key (mail)
	        	content.append(r.getKey());
	        	// ... tag if exist
	        	if(r.getColumnSlice().getColumnByName("tag") != null){
		        	String tag = r.getColumnSlice().getColumnByName("tag").getValue();
		        	if(tag != null && (!tag.isEmpty())){
			        	tag = Utils.tagsAsHtml(tag);
			        	content.append(String.format("(%s)", tag));     	        		
		        	}
	        	}
	        	// ... info if exist
	        	if(r.getColumnSlice().getColumnByName("info") != null){
		        	String info = r.getColumnSlice().getColumnByName("info").getValue();
		        	if(info != null && (!info.isEmpty()))
		        		content.append(String.format("<br /><i>%s</i>", info));
	        	}
	        	
	        	content.append("</div>");
	    	}
	    }
	    if(validRows == 0)
	    	content.append("...");
    	StringBuffer header = new StringBuffer();
    	header.append("Count of users: " + validRows);					
	    header.append(" | ");
	    header.append(Utils.createJSLinkHAAD(
					Utils.Q("AdmUsers"), 
					Utils.Q("addForm"), 
					Utils.Q(inAppID),
					Utils.Q("admin.app.action"), 
					"New"
					)
	    		);
			
	    header.append("<hr />");

		return(header.toString() + content.toString());
	}

	private static String getDeleteLink(
			String inAppID, 
			String key) {
		String jsData = Utils.jsData(
				 "handler", Utils.Q("AdmUsers")
				,"action",  Utils.Q("delete")
				,"appid", Utils.Q(inAppID)
				,"value", Utils.Q(key)
				,"dest", Utils.Q("admin.app.action")
			);
		return("[" + Utils.createJSLinkWithConfirm(jsData, "X") + "]");		
	}

}
