package org.hydra.deployers;

import java.net.URLConnection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Utils;

public class ApplicationFiles extends AMessageHandler {
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
		
		List<String> filePathes = FileUtils.getListOfFiles(inAppID);
		_log.debug("filePathes.size(): " + filePathes.size());
		for (String filePath : filePathes) {
    		String mimeType = URLConnection.guessContentTypeFromName(filePath);
    		if(mimeType == null) continue;
    		
	    	String divHiddenID = "template." + filePath;  
    		content.append("<div style=\"margin: 5px; padding: 5px; border: 1px solid rgb(127, 157, 185);\">");
    		
        	content.append(getDeleteLink(inAppID, filePath) + " ");
        	content.append("[<strong>" + mimeType + "</strong>] ");
        	
        	String htmlTag = "NOT_DEFINED";
    		if(mimeType.compareToIgnoreCase("image") >= 0){
    			htmlTag = Utils.F("<img src=\"%s\" border=\"0\">", filePath);
    		}else{
    			htmlTag = Utils.F("<a href=\"%s\" target=\"_blank\">TEXT</a>", filePath);
    		}
			content.append(getToogleLink(divHiddenID, filePath));
    		content.append(Utils.F("<div id=\"%s\" style=\"display: none;\" class=\"edit\">%s<hr />%s</div>", 
    				divHiddenID,
    				StringEscapeUtils.escapeHtml(htmlTag),
    				htmlTag));        	
        	// ... key 
        	
        	content.append("</div>");
	    }
	    if(filePathes.size() == 0)
	    	content.append("...");
    	StringBuffer header = new StringBuffer();
    	header.append("Count: " + filePathes.size());					
	    header.append(" | ");
	    header.append(Utils.createJSLinkHAAD(
					Utils.Q("AdmFiles"), 
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
	
	private static String getToogleLink(
			String divID, String key) {
		String format = "<a href=\"#\" title=\"Preview\" onclick=\"javascript:void(Globals.toogleBlock('%s')); return false;\">%s</a>";
		return(String.format(format, divID, key));
	}	
}
