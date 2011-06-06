package org.hydra.deployers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.MessagesManager;
import org.hydra.utils.DBUtils;
import org.hydra.utils.StringWrapper;
import org.hydra.utils.Utils;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

public final class SystemLogin {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemLogin");

	public static String getKeyHow(
			String inKey, 
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID) {
		
		if(inKey.compareToIgnoreCase("form") == 0)
			return getFormAny(inHow, inLocale, inApplicationID,inUserID);
		
		String tempStr = String.format("{{System|Loging|%s|%s}}",inKey, inHow);
		_log.error("Could not find KEY part for: " + tempStr);
		return tempStr ;
	}

	private static String getFormAny(
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID) {
		
		if(inUserID == null || inUserID.isEmpty())
			return getFormLogin(inHow, inLocale, inApplicationID);

		return(getUserInfo(inHow, inLocale, inApplicationID, inUserID));
	}

	private static String getUserInfo(
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID) {
		
		StringWrapper inValue = new StringWrapper();
		String tag = null;
		String info = null;
		
		ERROR_CODES err = DBUtils.getValue(inApplicationID, "User", inUserID, "tag", inValue);
		if(err == ERROR_CODES.NO_ERROR || err == ERROR_CODES.ERROR_DB_NULL_VALUE){
			if(inValue.getString() == null || inValue.getString().isEmpty()){
					//return Utils.T("html.user.info", inUserID, "...", inApplicationID);
				tag = "...";
			}
			tag =  Utils.tagsAsHtml(inValue.getString());
		}
		
		err = DBUtils.getValue(inApplicationID, "User", inUserID, "info", inValue);
		if(err == ERROR_CODES.NO_ERROR || err == ERROR_CODES.ERROR_DB_NULL_VALUE){
			if(inValue.getString() == null || inValue.getString().isEmpty()){
					info = "...";
			}
			info = inValue.getString();
		}		
		
		return Utils.T("html.user.info", inUserID, tag, info , inApplicationID);
	}

	private static String getFormLogin(
			String inHow, 
			String inLocale,
			String inApplicationID) {
		return(Utils.T("html.form.login", inApplicationID));
	}

}

