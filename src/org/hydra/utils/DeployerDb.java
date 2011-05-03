package org.hydra.utils;

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
			String inUserID, Moder inModer) {
		_log.debug("Enter to: getDbWhatKeyHow");
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getDbTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID, inModer);
		if(inWhat.compareToIgnoreCase("template") == 0)
			return getDbTemplateKeyHow(inKey, inHow, inApplicationID, inUserID, inModer);
		
		_log.warn(String.format("Could not find WHAT part for {{DB|%s|%s|%s}}", inWhat,inKey, inHow));
		return String.format("{{DB|%s|%s|%s}}", inWhat,inKey, inHow) ;
	}

	private static String getDbTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer){
		_log.debug("Enter to: getDbTextKeyHow");
		if(inHow.compareToIgnoreCase("locale") == 0)
			return getDbTextKeyLocale(inKey, inApplicationID, inLocale, inUserID, inModer);
		_log.error(String.format("Could not find WHAT part for {{DB|Text|%s|%s}}",inKey, inHow));
		return String.format("{{DB|Text|%s|%s}}",inKey, inHow);
	};
	
	private static String getDbTemplateKeyHow(
			String inKey,
			String inHow,			 // reserved
			String inApplicationID,  // reserved
			String inUserID, 		 // reserved
			Moder inModer            // reserved
			) {
		_log.debug("Enter to: getDbTemplateKeyHow");
		// get result from DB
		StringWrapper content = new StringWrapper();
		ERROR_CODES err = DBUtils.getValue(inApplicationID, "Template", inKey, "html", content);
		switch (err) {
		case NO_ERROR:
			if(Utils.hasRight4Template(inApplicationID, inUserID, inModer))
				makeJSEditLink4Template(inKey, content, "DBRequest", "editTemplate", "updateTemplate");
			break;
		case ERROR_NO_VALUE:
		case ERROR_NO_CF_BEAN:
		case ERROR_NO_DATABASE:
			if(Utils.hasRight4Template(inApplicationID, inUserID, inModer)){
				content.setString(String.format("{{DB|Template|%s|%s}}: %s",inKey, inHow, err.toString()));
				makeJSEditLink4Template(inKey, content, "DBRequest", "editTemplate", "updateTemplate");
			}else content.setString("");
			break;
		default:
			if(Utils.hasRight4Template(inApplicationID, inUserID, inModer)){
				content.setString(String.format("{{DB|Template|%s|%s}}: UNKOWN ERROR",inKey, inHow));
			}else content.setString("");
			break;
		}

		return content.getString();
	}

	private static String getDbTextKeyLocale(
			String inKey,
			String inApplicationID, 
			String inLocale,
			String inUserID, 
			Moder inModer) {
		_log.debug("Enter to: getDbTextKeyLocale");		
		// get result from DB
		StringWrapper content = new StringWrapper();
		ERROR_CODES err = DBUtils.getValue(inApplicationID, "Text", inKey, inLocale, content);
		switch (err) {
		case NO_ERROR:
			if(Utils.hasRight4Text(inApplicationID, inUserID, inModer))
				makeJSEditLink4Text(inKey, content, "DBRequest", "editText", "updateText", false);	
			break;
		case ERROR_NO_VALUE:
		case ERROR_NO_CF_BEAN:
		case ERROR_NO_DATABASE:
			if(Utils.hasRight4Text(inApplicationID, inUserID, inModer)){
				content.setString(String.format("{{DB|Text|%s|locale}}: %s",inKey, err.toString()));
				makeJSEditLink4Text(inKey, content, "DBRequest", "editText", "updateText", true);		
			}else content.setString("&nbsp;");			
			break;
		default:
			if(Utils.hasRight4Text(inApplicationID, inUserID, inModer)){
				content.setString(String.format("{{DB|Text|%s|locale}}: UNKOWN ERROR", inKey));
			}else content.setString("&nbsp;");				
			break;
		}

		return content.getString();
	}

	private static void makeJSEditLink4Text(
			String inKey, 
			StringWrapper content, 
			String inHandleName,
			String inEditActionName,
			String inUpdateActionName, 
			boolean isError) {
		StringBuffer resultBuffer = new StringBuffer();
		
		resultBuffer.append("<span class='editorlinks' id='").append(inKey).append(".editorlinks'><sup>");
		if(isError)
			resultBuffer.append("<a class='red' onclick=\"javascript:void(Globals.editIt('");
		else
			resultBuffer.append("<a class='green' onclick=\"javascript:void(Globals.editIt('");
		resultBuffer.append(inKey).append("','").append(inHandleName).append("','").append(inEditActionName)
					.append("')); return false;\" href=\"#\">").append(inKey).append("</a>");		
		resultBuffer.append("</sup></span><br />");
		
		resultBuffer.append(" <span id='").append(inKey).append("'>").append(isError?"":content.getString()).append("</span>");
		
		content.setString(resultBuffer.toString());
	}	
	
	private static void makeJSEditLink4Template(
			String inKey, 
			StringWrapper content, 
			String inHandleName,
			String inEditActionName,
			String inUpdateActionName) {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("<div class='edit'>");
		
		resultBuffer.append("&nbsp;<sup class='editorlinks' id='").append(inKey).append(".editorlinks").append("'>");
		resultBuffer.append("<a onclick=\"javascript:void(Globals.editIt('")
					.append(inKey).append("','").append(inHandleName).append("','").append(inEditActionName)
					.append("')); return false;\" href=\"#\">").append(inKey).append("</a>");		
		resultBuffer.append(" </sup>");
		
		resultBuffer.append(" <div id='").append(inKey).append("'>").append(content.getString()).append("</div>");
		resultBuffer.append("</div>");
		content.setString(resultBuffer.toString());
	}
}
