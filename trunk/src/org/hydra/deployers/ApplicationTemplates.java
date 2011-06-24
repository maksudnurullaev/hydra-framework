package org.hydra.deployers;

import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public class ApplicationTemplates extends AMessageHandler {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationTemplates");

	static String getKeyHow(
			String inKey, 
			String inHow) {
		if(inHow.compareToIgnoreCase("html") == 0)
			return getKeyHtml(inKey);
		
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;		
	}

	static String getKeyHtml(
			String inAppID) {
		StringBuffer content = new StringBuffer();
		
		List<Row<String,String,String>> rows = DBUtils.getValidRows(inAppID, "Template", "", "", "", "");
		_log.debug("rows.size(): " + rows.size());
		int validRows = 0;

	    for (Row<String, String, String> r : rows) {
	    	HColumn<String, String> colContent = r.getColumnSlice().getColumnByName("content");
	    	if(colContent == null){
	    		_log.warn(Utils.F("Could not find value for Template[%s][content]!", r.getKey()));
	    		continue;
	    	}
	    	validRows++;
	    	String divHiddenID = "template." + r.getKey();  
    		content.append("<div style=\"margin: 5px; padding: 5px; border: 1px solid rgb(127, 157, 185);\">");
        	
        	// ... delete
        	content.append(getDeleteLink(inAppID, r.getKey()) + " ");
        	// ... edit
        	content.append(getUpdateLink(inAppID, r.getKey()) + " ");        	
        	// ... key 
        	content.append(getToogleLink(divHiddenID, r.getKey()));

        	// ... tag if exist
        	if(r.getColumnSlice().getColumnByName("tag") != null){
	        	String tag = r.getColumnSlice().getColumnByName("tag").getValue();
	        	if(tag != null && (!tag.isEmpty())){
		        	tag = Utils.tagsAsHtml(tag);
		        	content.append(" " + tag);     	        		
	        	}
        	}
        	// ... content size
       		content.append(String.format(" (<i>SIZE:%s</i>)", colContent.getValue().length()));
        	
    		content.append(Utils.F("<div id=\"%s\" style=\"display: none;\" class=\"edit\">%s</div>", divHiddenID, Utils.escapeHtmlAndMyTags(colContent.getValue())));        	
        	content.append("</div>");
	    }
	    if(validRows == 0)
	    	content.append("...");
    	StringBuffer header = new StringBuffer();
    	header.append("Count: " + validRows);					
	    header.append(" | ");
	    header.append(Utils.createJSLinkHAAD(
					Utils.Q("AdmTemplates"), 
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
				 "handler", Utils.Q("AdmTemplates")
				,"action",  Utils.Q("delete")
				,"appid", Utils.Q(inAppID)
				,"key", Utils.Q(key)
				,"dest", Utils.Q("admin.app.action")
			);
		return(Utils.F("[%s]", Utils.createJSLinkWithConfirm("Delete",jsData, "X")));		
	}
	
	private static String getUpdateLink(
			String inAppID, 
			String key) {
		String jsData = Utils.jsData(
				 "handler", Utils.Q("AdmTemplates")
				,"action",  Utils.Q("updateForm")
				,"appid", Utils.Q(inAppID)
				,"key", Utils.Q(key)
				,"dest", Utils.Q("admin.app.action")
			);
		return(Utils.F("[%s]", Utils.createJSLink("Update",jsData, "U")));		
	}
	
	private static String getToogleLink(
			String divID, String key) {
		String format = "<a href=\"#\" title=\"Preview\" onclick=\"javascript:void(Globals.toogleBlock('%s')); return false;\">%s</a>";
		return(String.format(format, divID, key));
	}	
}
