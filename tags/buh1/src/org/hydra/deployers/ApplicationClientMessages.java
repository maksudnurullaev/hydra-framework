package org.hydra.deployers;

import java.util.List;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;

import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class ApplicationClientMessages {
	static final String _cfName = "ClientMessage";

	private static String getALLDivId(
			String inAppID, String inDivId) {
		StringBuffer content = new StringBuffer();
		content.append("<h2>[[Dictonary|Text|Client_Requests|span]]</h2>");
		content.append("<div id='app_action'>");
		List<Row<String,String,String>> rows = DBUtils.getRows(inAppID, _cfName, "", "", "", "");
		int validRows = 0;
	    for (Row<String, String, String> r : rows) {
	    	ColumnSlice<String, String> cs= r.getColumnSlice();
	    	if(cs != null && cs.getColumnByName("text") != null){
	    		HColumn<String, String> cText = cs.getColumnByName("text");
				StringBuffer content2 = new StringBuffer();
				content2.append(cText.getValue());
	    		content.append(getActiveBox(inAppID, r.getKey(), content2.toString(), inDivId));
	    		validRows++;
	    	}
	    }
	    if(validRows == 0)
	    	content.append("<div>...</div>");
		content.append("</div>");
		return content.toString();
	}

	static String getActiveBox(String inAppID, String inKey, String inContent, String inDivId) {
		StringBuffer content = new StringBuffer();
		
    	String divHiddenID = Utils.GetUUID();  
		content.append("<div class='row'>");
		
    	content.append(getDeleteLink(inAppID, inKey, inDivId) + " " + inKey.substring(0, 19));
    	
    	if(inContent.length() > 40){
    		content.append(" ");
    		String linkTitle = ( inContent.length() > 40 ? inContent.substring(0, 36) + "..." : inContent );
    		content.append(
    				String.format(
    						"<a href=\"#\" title=\"Preview\" onclick=\"javascript:void(Globals.toogleBlock('%s')); return false;\">%s</a>", 
    						divHiddenID, 
    						linkTitle));    	
    		content.append(Utils.F("<div class='row' id=\"%s\"  style=\"display: none;\">%s</div>", 
    				divHiddenID,
    				inContent));        	
    	}else{
    		content.append(" " + inContent);
    	}
    	
    	content.append("</div>");
    	
		return content.toString();
	}	
	
	public static String getDeleteLink(
			String inAppID, 
			String key, 
			String inDivId) {
		String jsData = Utils.jsData(
				 "handler", Utils.Q(_cfName)
				,"action",  Utils.Q("delete")
				,"appid", Utils.Q(inAppID)
				,"key", Utils.Q(key)
				,"dest", Utils.Q(Utils.sanitazeHtmlId(inDivId))
		);
		return(Utils.F("[%s]", Utils.createJSLinkWithConfirm("Delete",jsData, "X")));		
	}	
	
	static String getKeyHow(
			String inKey, // AppID
			String inHow) {
		
		return getALLDivId(inKey, inHow);
	}

}
