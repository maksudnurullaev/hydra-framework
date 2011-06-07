package org.hydra.deployers;

import java.util.List;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;

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
		
		String tag = null;
		String info = null;
		
		if(inUserID.length() == 3 && inUserID.contains("+++")){
			tag = "+++";
			info = "+++";
		}else{
			List<Row<String,String,String>> rows = DBUtils.getValidRows(inApplicationID, "User", inUserID, inUserID, "", "");
			if(rows == null || rows.size() != 1){
				return "NO_UNIQUE";
			}
			Row<String,String,String> row = rows.get(0);
			ColumnSlice<String, String> cs = row.getColumnSlice();
			HColumn<String, String> col = cs.getColumnByName("tag");
			if(col != null)
				tag = Utils.tagsAsHtml(col.getValue());
			col = cs.getColumnByName("info");
			if(col != null)
				info = col.getValue();
		}
		return Utils.T("html.user.panel", inUserID, tag, info , inApplicationID);
	}

	private static String getFormLogin(
			String inHow, 
			String inLocale,
			String inApplicationID) {
		return(Utils.T("html.form.login", inApplicationID));
	}

}

