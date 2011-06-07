package org.hydra.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.html.fields.IField;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

/**
 * @author M.Nurullayev
 */
public final class Utils {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.Utils");

	public static String wrap2HTMLTag(String inHTMLTagName, String inContent) {
		return String.format("<%s>%s</%s>", inHTMLTagName, inContent,
				inHTMLTagName);
	}

	public static boolean isInvalidString(String inSting) {
		return (inSting == null || inSting.trim().isEmpty());
	}

	/**
	 * Validate the form of an email address.
	 * 
	 * @param aEmailAddress
	 *            that will be compiled against:
	 *            <p>
	 *            {@code Pattern.compile(".+@.+\\.[a-z]+")}
	 *            </p>
	 * @return (boolean)true or false
	 * 
	 */
	public static boolean isValidEmailAddress(String aEmailAddress) { // NO_UCD
		Matcher m = Constants.p.matcher(aEmailAddress);
		return m.matches();
	}

	public static String trace(Object inObj,
			StackTraceElement[] stackTraceElements) {
		String format = inObj.getClass().getSimpleName() + ".%s:\n";

		boolean doNext = false;
		for (StackTraceElement s : stackTraceElements) {
			if (doNext) {
				return String.format(format, s.getMethodName());
			}
			doNext = s.getMethodName().equals("getStackTrace");
		}

		return String.format(format, "no-stacktrace-found!");
	}

	public static String T(String inTemplateName, Object... inStrings) {
		return (String.format(MessagesManager.getTemplate(inTemplateName),
				inStrings));
	}

	public static String GetDateUUID() { // NO_UCD
		return Utils.GetCurrentDateTime(Constants.time_uid_format) + " - "
				+ Utils.GetUUID();
	}

	public static String GetUUID() {
		return java.util.UUID.randomUUID().toString();

	}

	public static String GetCurrentDateTime(String inFormat) { // NO_UCD
		SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
		return sdf.format(new Date());
	}

	public static String GetCurrentDateTime() { // NO_UCD
		SimpleDateFormat sdf = new SimpleDateFormat();
		return String.format(sdf.format(new Date()));
	}

	public static String getNewLine() {
		return String.format("%n");
	}

	public static void getFileAsString(Result inResult, String inPath2File) {
		try {
			File file = new File(inPath2File);
			inResult.setObject(FileUtils
					.readFileToString(file, Constants._utf8));
			inResult.setResult(true);
		} catch (Exception e) {
			_log.error(e.getMessage());
			inResult.setResult("Internal server error: INITIAL_FILE_NOT_FOUND");
			inResult.setResult(false);
		}
	}

	// **** Moders & Rights
	public static boolean hasRight2Edit(
			String inApplicationID,
			String inUserID) {
		return (test4Roles(inApplicationID, inUserID, "User.Editor", "User.Publisher", "User.User.Administrator"));
	}

	public static String shrinkString(String inString) {
		if (inString == null)
			return "NULL";
		inString = inString.trim();
		if (inString.length() > 10)
			inString = inString.substring(0, 7) + "...";
		return inString;
	}

	public static String formatEditLinks(List<String> links) {
		if (links == null || links.size() == 0)
			return "CLOSE_ME: " + (new Date(System.currentTimeMillis())).toString();
		StringBuffer result = new StringBuffer();
		for (String link : links) {
			if (result.length() != 0)
				result.append(" ");
			result.append(link);
		}
		result.append("<div id=\"editBox\"></div>");
		return result.toString();
	}

	public static String createJSLinkHAAD(
			String inHandler
			, String inMethod
			, String inKey
			, String inDest
			, String inName
			) {
		String jsData = Utils.T("template.html.js.HAAD"
				, inHandler
				, inMethod
				, inKey
				, inDest);
		return Utils.T("template.html.a.onClick.sendMessage.Label"
				, jsData
				, inName);
	}

	public static String createJSLink(
			String inJSData
			, String inName
			) {
		return Utils.T("template.html.a.onClick.sendMessage.Label"
				, inJSData
				, inName);
	}

	public static String V(String id) {
		return "$('" + id + "').value" ; 
	}
	public static String Q(String inString) {
		return ("'" + inString + "'");
	}

	public static String QQ(String inString) {
		return ("\"" + inString + "\"");
	}

	public static String jsData(String... strings) {
		if (strings == null || strings.length == 0
				|| ((strings.length % 2) != 0))
			return "jsDataError";
		StringBuffer ss = new StringBuffer();
		boolean isKeyPart = false;
		for (String string : strings) {
			isKeyPart = !isKeyPart; // switch key part
			if (ss.length() > 0 && (isKeyPart))
				ss.append(",");
			ss.append(string);
			if (isKeyPart)
				ss.append(":");
		}
		return ss.toString();
	}

	public static String createJSLinkWithConfirm(String inJSData, String inName) {
		return Utils.T("template.html.a.onClick.confirmAndSendMessage.Label"
				, inJSData
				, inName);
	}

	public static String generateForm(
			String inTitle,
			String inAppId,
			String inSaveHandler, String inSaveAction, // Save
			String inCancelHandler, String inCancelAction, // Cancel
			String inDest,
			ArrayList<IField> fields, 
			ArrayList<IField> optionaFields) {

		List<String> strSaveArrayData = new ArrayList<String>();
		strSaveArrayData.add("appid");
		strSaveArrayData.add(Utils.Q(inAppId));
		strSaveArrayData.add("handler");
		strSaveArrayData.add(Utils.Q(inSaveHandler));
		strSaveArrayData.add("action");
		strSaveArrayData.add(Utils.Q(inSaveAction));
		strSaveArrayData.add("dest");
		strSaveArrayData.add(Utils.Q(inDest));
		
		if(fields != null && fields.size() > 0){			
			for (IField s : fields) {
				strSaveArrayData.add(s.getID());
				strSaveArrayData.add(s.getValue4JS());
			}
		}
		
		if(optionaFields != null && optionaFields.size() > 0){
			for (IField s : optionaFields) {
				strSaveArrayData.add(s.getID());
				strSaveArrayData.add(s.getValue4JS());
			}
		}
		
		String jsSaveData = jsData(strSaveArrayData
				.toArray(new String[0]));

		List<String> strCancelArrayData = new ArrayList<String>();
		strCancelArrayData.add("appid");
		strCancelArrayData.add(Utils.Q(inAppId));
		strCancelArrayData.add("handler");
		strCancelArrayData.add(Utils.Q(inCancelHandler));
		strCancelArrayData.add("action");
		strCancelArrayData.add(Utils.Q(inCancelAction));
		strCancelArrayData.add("dest");
		strCancelArrayData.add(Utils.Q(inDest));

		String jsCancelData = jsData(strCancelArrayData
				.toArray(new String[0]));

		StringBuffer ssJsActions = new StringBuffer();
		ssJsActions.append(Utils.T("template.html.a.onClick.sendMessage.Label"
				, jsSaveData
				, "Save"));
		ssJsActions.append(" | ");
		ssJsActions.append(Utils.T("template.html.a.onClick.sendMessage.Label"
				, jsCancelData
				, "Cancel"));

		String jsActions = ssJsActions.toString();

		StringBuffer result = new StringBuffer(inTitle);

		result.append("<table class=\"statistics\">");
		result.append("<tbody>");
		if(fields != null){
			for (IField s : fields)
				result.append(String.format(
						"<tr><td class=\"tr\">%s:</td><td>%s</td></tr>"
								, String.format("[[DB|Text|%s|locale]]", s.getID())
								, s.getAsHtml()));
		}
		if(optionaFields != null){
			result.append("<tr><td colspan=\"2\"><u><i>[[DB|Text|additional|local]]</i></u></td></tr>");			
			for(IField s :optionaFields)
				result.append(String.format(
						"<tr><td class=\"tr\">%s:</td><td>%s</td></tr>"
								, String.format("[[DB|Text|%s|locale]]", s.getID())
								, s.getAsHtml()));
		}
		result.append(String.format("<tr><td>&nbsp;</td><td>%s</td></tr>",
				jsActions));
		result.append("</tbody>");
		result.append("</table>");

		return (result.toString());
	}

	public static List<String> getAllTags4(String inAppID, String inKeyRangeStart, String inKeyRangeFinish) {
		List<String> result = new ArrayList<String>();
		// set flobal tags
		for(String tag:Constants._GLOBAL_TAGS)
			result.add(tag);
		List<Row<String, String, String>> rows = DBUtils.getValidRows(inAppID, "Tag", "", "", inKeyRangeStart, inKeyRangeStart);
	    for (Row<String, String, String> r : rows) {
	        HColumn<String, String> colResult = 
	        	DBUtils.getColumn(inAppID, "Tag", r.getKey(), "name");
	        if(colResult != null 
	        		&& colResult.getValue() != null
	        		&& colResult.getValue().compareTo(inKeyRangeStart) >= 0){
	        	result.add(colResult.getValue());
	        }
	    }
		// finish
		return(result);
	}

	public static void testFieldEMail(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String mailString, 
			String fieldId) {
		if(mailString == null || (!isValidEmailAddress(mailString))){
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_MAIL);
			errorFields.add(fieldId);
		}
	}

	public static void test2ValidPasswords(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			CommonMessage inMessage, 
			String key, 
			String key2) {
		String value = inMessage.getData().get(key).trim();
		String value2 = inMessage.getData().get(key2).trim();
		
		if((value == null) ||
				(!value.equals(value2)) ||
				(value.length() < 5)){
			errorFields.add(key);
			errorFields.add(key2);
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_PASSWORDS);
		}
			
	}

	public static String getJsHighlight4(List<String> errorFields) {
		StringBuffer ss = new StringBuffer();
		ss.append("Globals.makeRedBorder4([");
		for (int i = 0; i < errorFields.size(); i++) {
			if(i != 0) ss.append(",");
			ss.append("\"" + errorFields.get(i) + "\"");
		}
		ss.append("]);");
		return ss.toString();
	}

	public static String tagsAsHtml(String value){
		String[] arr = value.split(",");
		String result = "";
		for(String t:arr){
			if(!result.isEmpty()) result += ", ";
			result += String.format("[[DB|Text|%s|locale]]", t);
		}
		return(result);
	}	
	
	public static String tagsAsEditableHtml(
			String appId, 
			String elemID,
			String value,
			String addValue,
			String delValue,
			List<String> tagPrefixes) {
		
		if(tagPrefixes == null || tagPrefixes.size() == 0) return "No tag prefixes";
		// add value
		if(addValue != null){
			if(value == null || value.isEmpty())
				value = addValue;
			else
				value += ("," + addValue); 
		}
		// delete value
		if(delValue != null){
			if(value != null && (!value.isEmpty()))
				value = del4Tags(value, delValue);
		}

		String selectID = "tag.select." + elemID;
		String prefixesID = "tag.prefixes." + elemID; 
		String divId = "tag.div." + elemID;
		
		StringBuffer ssPart = new StringBuffer();
		// input - value
		String inputHtmlTag = String.format("<input id=\"%s\" type=\"hidden\" value=\"%s\">", 
				elemID,
				value);
		// input - prefixes
		String prefixesValue = list2String(tagPrefixes);
		String prefixesHtmlTag = String.format("<input id=\"%s\" type=\"hidden\" value=\"%s\">", 
				prefixesID,
				prefixesValue);
		
		// select
		ssPart.append(String.format("<select id=\"%s\" style=\"border: 1px solid rgb(127, 157, 185);\">", selectID));
		boolean selectHasElements = false;
		if(tagPrefixes != null && tagPrefixes.size() > 0){
			String[] arrOfTags = value.split(",");
			for(String prefix:tagPrefixes){
				for(String tag:Utils.getAllTags4(appId, prefix, prefix)){
					if(containsTag(arrOfTags, tag)){// already exit 
						continue;
					}else{
						ssPart.append(String.format("<option value=\"%s\">[[DB|Text|%s|locale]]</option>", tag, tag));
						selectHasElements = true;
					}
				}
			}
		}
		ssPart.append("</select> | ");
		
		String jsData = Utils.jsData(
				 "handler", Utils.Q("Tagger")
				,"action",  Utils.Q("add")
				,"appid", Utils.Q(appId)
				,"elemid", Utils.Q(elemID)
				,"value", Utils.V(elemID)
				,"addvalue", Utils.V(selectID)
				,"prefixes", Utils.V(prefixesID)
				,"dest", Utils.Q(divId)
			);			
		ssPart.append(Utils.createJSLink(jsData, "Add"));		
		
		// div for tags
		String textPart = "";
		if(value.isEmpty()){
			textPart = "...";
		}else{
			textPart = "";
			String[] arr = value.split(",");
			for(String t:arr){
				if(!textPart.isEmpty()) textPart += ", ";
				textPart += String.format("[[DB|Text|%s|locale]]", t);
				jsData = Utils.jsData(
						 "handler", Utils.Q("Tagger")
						,"action",  Utils.Q("delete")
						,"appid", Utils.Q(appId)
						,"elemid", Utils.Q(elemID)
						,"value", Utils.V(elemID)
						,"delvalue", Utils.Q(t)
						,"prefixes", Utils.V(prefixesID)
						,"dest", Utils.Q(divId)
					);			
				textPart += ("[" + Utils.createJSLink(jsData, "X") + "]");	
			}
			
		}
		
		// finish
		String result = "";
		result += String.format("<div id='%s'>", divId);
		result += textPart;
		if(selectHasElements)
			result += ("<hr />" + ssPart.toString());
		result += (inputHtmlTag + prefixesHtmlTag);
		result += "</div>";
		
		return(result);  		
	}

	private static boolean containsTag(String[] arrOfTags, String tag) {
		for(String t: arrOfTags){
			if(tag.length() == t.length() && tag.compareTo(t) == 0)
				return true;
		}
		return false;
	}

	private static String del4Tags(String value, String delValue) {
		String[] arr = value.split(",");
		String result = "";
		for(String t: arr){
			if(t.compareTo(delValue) == 0) continue;
			if(!result.isEmpty()) result += ",";
			result += t;
		}
		return result;
	}

	public static String list2String(List<String> values) {
		return list2String("", values, ",");
	}
	
	public static String list2String(
			String prefix,
			List<String> values, 
			String postfix) {
		String result = "";
		for(String value:values){
			if(!result.isEmpty()) result += postfix;
				result += (prefix + value);
		}
		return result;
	}

	public static List<String> string2List(String values, String delimiter) {
		List<String> result = new ArrayList<String>();
		for(String value: values.split(","))
			result.add(value);
		
		return result;
	}

	public static String getErrorDescription(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes) {
		String result = "Error_codes:\n" + listOfError2String("\t", errorCodes, "\n");
		result += "\nError_fields:\n" + list2String("\t", errorFields, "\n");
		return result;
	}

	private static String listOfError2String(
			String prefix,
			List<ERROR_CODES> errorCodes, 
			String postfix) {
		String result = "";
		for(ERROR_CODES errorCode:errorCodes){
			if(!result.isEmpty()) result += postfix;
				result += (prefix + errorCode.toString());
		}
		return result;
	}


	public static void test2ValidPassword(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String value, 
			String elemId) {
		
		if((value == null) || (value.length() < 5)){
			errorFields.add(elemId);
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_PASSWORD);
		}
			
	}

	public static boolean isSpecialKey(String inKey) {
		if(inKey == null || inKey.length() == 0) return false;
		return(inKey.startsWith("_"));
	}

	public static boolean test4Roles(String inApplicationID, String inUserID, String...roles) {
		if(inApplicationID == null || inApplicationID.length() == 0) return false;
		if(inUserID == null || inUserID.length() == 0) return false;
		if(roles == null || roles.length == 0) return false;

		if(inUserID.startsWith("+++")) return true; // super user
		
		StringWrapper sWrapper = new StringWrapper();
		ERROR_CODES err = DBUtils.getValue(inApplicationID, "User", inUserID, "tag", sWrapper);
		if(err == ERROR_CODES.NO_ERROR && !sWrapper.getString().isEmpty()){
			for(String role:roles){
				if(sWrapper.getString().contains(role)) return true;
			}
		}
		return false;
	}
}
