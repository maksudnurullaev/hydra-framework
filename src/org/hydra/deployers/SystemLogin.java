package org.hydra.deployers;

import java.util.List;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

public final class SystemLogin {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.SystemLogin");

	public static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage
			) {
		
		if(inKey.compareToIgnoreCase("form") == 0)
			return getFormAny(inHow, inMessage);
		else if(inKey.compareToIgnoreCase("Info") == 0 &&
				inHow.compareToIgnoreCase("short") == 0)
			return getUserInfoShort(inHow, inMessage, true);
		
		String tempStr = String.format("ERROR: {{System|Login|&gt;%s&lt;|&gt;%s&lt;}}",inKey, inHow);
		_log.error("Could not find KEY part for: " + tempStr);
		return tempStr ;
	}

	private static String getFormAny(
			String inHow, 
			IMessage inMessage) {
		String userId = inMessage.getData().get("_userid");
		String appId = inMessage.getData().get("appid");
		String locale = inMessage.getData().get("locale");
		if(userId == null || userId.isEmpty())
			return getFormLogin(inHow, locale);

		return(getUserInfo(inHow, locale, appId, userId, false));
	}

	private static String getUserInfoShort(
			String inHow, 
			IMessage inMessage,
			boolean inShort) {
		String userId = inMessage.getData().get("_userid");
		String appId = inMessage.getData().get("appid");
		String locale = inMessage.getData().get("locale");

		return(getUserInfo(inHow, locale, appId, userId, inShort));
	}	
	private static String getUserInfo(
			String inHow, 
			String inLocale,
			String inApplicationID, 
			String inUserID, 
			boolean inShort) {
		
		String tag = "";
		String info = "";
		
		if(inUserID.length() == 1 && inUserID.contains("*")){
			tag = "*";
			info = "*";
		}else{
			List<Row<String,String,String>> rows = DBUtils.getRows(inApplicationID, "User", inUserID, inUserID, "", "");
			
			if(rows == null)     return "NO_USER";
			if(rows.size() != 1) return "NOT_UNIQUE";
			
			Row<String,String,String> row = rows.get(0);
			ColumnSlice<String, String> cs = row.getColumnSlice();
			HColumn<String, String> col = cs.getColumnByName("tag");
			if(col != null && col.getValue() != null && !col.getValue().isEmpty())
				tag = Utils.tagsAsHtml(col.getValue());
			col = cs.getColumnByName("info");
			if(col != null && col.getValue() != null && !col.getValue().isEmpty())
				info = col.getValue();
		}
		if(inShort){
			return Utils.T("html.user.login.info.short", inUserID, inApplicationID);			
		} else {
			return Utils.T("html.user.login.info", 
					inUserID, 
					((tag == null || tag.isEmpty()) ? "---" : tag), 
					((info == null || info.isEmpty()) ? "---" : info) , inApplicationID);
		}
	}

	private static String getFormLogin(
			String inHow, 
			String inLocale) {
		return(Utils.T("html.form.login"));
	}

}

