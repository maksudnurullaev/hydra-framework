package org.hydra.utils;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils.ERROR_CODES;


public final class DeployerDb {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DeployerDb");

	public static String getDbWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links) {
		_log.debug("Enter to: getDbWhatKeyHow");
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getDbTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, inModer, links);
		if(inWhat.compareToIgnoreCase("template") == 0)
			return getDbTemplateKeyHow(inKey, inHow, inApplicationID, inUserID, inModer, links);
		
		_log.warn(String.format("Could not find WHAT part for {{DB|%s|%s|%s}}", inWhat,inKey, inHow));
		return String.format("{{DB|%s|%s|%s}}", inWhat,inKey, inHow) ;
	}

	private static String getDbTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("locale") == 0)
			return getDbTextKeyLocale(inKey, inApplicationID, inLocale, inUserID, inModer, links);
		_log.error(String.format("Could not find WHAT part for {{DB|Text|%s|%s}}",inKey, inHow));
		return String.format("{{DB|Text|%s|%s}}",inKey, inHow);
	};
	
	private static String getDbTemplateKeyHow(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			Moder inModer,            // reserved
			List<String> links) {
		_log.debug("Enter to: getDbTemplateKeyHow");
		// get result from DB
		StringWrapper content = new StringWrapper();
		ERROR_CODES err = DBUtils.getValue(inApplicationID, "Template", inKey, "html", content);
		switch (err) {
		case NO_ERROR:
			if(Utils.hasRight2Edit(inApplicationID, inUserID, inModer))
				wrap2SpanEditObject(inKey, content, "DBRequest", "Template", false, links);
			break;
		case ERROR_NO_VALUE:
		case ERROR_NO_CF_BEAN:
		case ERROR_NO_DATABASE:
			_log.error(String.format("DB error with %s: %s", inKey, err.toString()));
			content.setString(String.format("<font color='red'>%s</font>",inKey, err.toString()));
			if(Utils.hasRight2Edit(inApplicationID, inUserID, inModer))
				wrap2SpanEditObject(inKey, content, "DBRequest", "Template", true, links);
			break;
		default:
			_log.error(String.format("DB error with %s: UNKNOWN ERR_CODE", inKey));
			content.setString(String.format("<font color='red'>%s</font>",inKey, err.toString()));
			break;
		}

		return content.getString();
	}

	private static String getDbTextKeyLocale(
			String inKey,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer,
			List<String> links) {
		_log.debug("Enter to: getDbTextKeyLocale");		
		// get result from DB
		StringWrapper content = new StringWrapper();
		ERROR_CODES err = DBUtils.getValue(inApplicationID, "Text", inKey, inLocale, content);
		switch (err) {
		case NO_ERROR:
			if(Utils.hasRight2Edit(inApplicationID, inUserID, inModer))
				wrap2SpanEditObject(inKey, content, "DBRequest", "Text", false, links);	
			break;
		case ERROR_NO_VALUE:
		case ERROR_NO_CF_BEAN:
		case ERROR_NO_DATABASE:
			_log.error(String.format("DB error with %s: %s", inKey, err.toString()));
			content.setString(String.format("<font color='red'>%s</font>",inKey, err.toString()));
			if(Utils.hasRight2Edit(inApplicationID, inUserID, inModer))
				wrap2SpanEditObject(inKey, content, "DBRequest", "Text", true, links);		
			break;
		default:
			_log.error(String.format("DB error with %s: UNKNOWN ERR_CODE", inKey));
			content.setString(String.format("<font color='red'>%s</font>",inKey, err.toString()));
			break;
		}

		return content.getString();
	}

	private static void wrap2SpanEditObject(
			String inKey, 
			StringWrapper content, 
			String inHandleName,
			String inEditObjectName,
			boolean isError, List<String> links) {

		String wrapString = String.format("<div class='edit' id='%s'>%s</div>", inKey, content.getString());
		content.setString(wrapString.toString());
		// List of Link
		if(links != null){
			StringBuffer result = new StringBuffer();
			
			// main link
			if(isError)
				result.append("<a class='red' onclick=\"javascript:void(Globals.editIt('");
			else
				result.append("<a class='green' onclick=\"javascript:void(Globals.editIt('");
			result.append(inKey).append("','").append(inHandleName).append("','").append("edit" + inEditObjectName)
						.append("')); return false;\" href=\"#\">").append(inKey).append("</a>");
			// sup - description
			result.append("<sup>(<a class='green' onclick=\"javascript:void(Globals.blinkIt('");
			result.append(inKey).append("')); return false;\" href=\"#\">").append(inEditObjectName).append("</a>)</sup>");
			
			links.add(result.toString());
		}
	}
}
