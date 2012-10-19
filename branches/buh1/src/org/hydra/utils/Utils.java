package org.hydra.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.html.fields.FieldInput;
import org.hydra.html.fields.IField;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.interfaces.IMessage;
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
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.time_uid_format);
		return String.format(sdf.format(new Date()));
	}

	public static String getNewLine() {
		return String.format("%n");
	}

	public static void getFileAsString(Result inResult, String inPath2File) {
		try {
			File file = new File(inPath2File);
			inResult.setObject(FileUtils
					.readFileToString(file, Constants._utf_8));
			inResult.setResult(true);
		} catch (Exception e) {
			_log.error(e.getMessage());
			inResult.setErrorString("Internal server error: INITIAL_FILE_NOT_FOUND");
			inResult.setResult(false);
		}
	}

	public static String shrinkString(String inString) {
		if (inString == null)
			return "NULL";
		inString = inString.trim();
		if (inString.length() > 10)
			inString = inString.substring(0, 7) + "...";
		return inString;
	}

	public static String formatEditLinks(Map<String, String> editLinks) {
		if (editLinks == null || editLinks.size() == 0)
			return "CLOSE_ME: " + (new Date(System.currentTimeMillis())).toString();
		StringBuffer result = new StringBuffer();
		for (Map.Entry<String, String> link : editLinks.entrySet()) {
			if (result.length() != 0)
				result.append(" ");
			result.append(link.getValue());
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

	public static String createJSLink(
			  String inTitle
			, String inJSData
			, String inName
			) {
		return Utils.T("template.html.a.Title.onClick.sendMessage.Label"
				, inTitle
				, inJSData
				, inName);
	}	
	
	public static String V(String id) {
		return "jQuery('#" + id + "').prop('value')" ; 
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
	
	public static String createJSLinkWithConfirm(String inTitle, String inJSData, String inName) {
		return Utils.T("template.html.a.Title.onClick.confirmAndSendMessage.Label"
				, inTitle
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
			ArrayList<IField> optionalFields, 
			CommonMessage inMessage) {

		List<String> strSaveArrayData = new ArrayList<String>();
		if(inAppId != null){
			strSaveArrayData.add("appid");
			strSaveArrayData.add(Utils.Q(inAppId));
		}
		strSaveArrayData.add("handler");
		strSaveArrayData.add(Utils.Q(inSaveHandler));
		strSaveArrayData.add("action");
		strSaveArrayData.add(Utils.Q(inSaveAction));
		strSaveArrayData.add("dest");
		strSaveArrayData.add(Utils.Q(Utils.sanitazeHtmlId(inDest)));
		
		String fileField = null;
		
		if(fields != null && fields.size() > 0){			
			for (IField field : fields) {
				if(isFieldFileUploadType(field)){
					fileField = field.getValue4JS();
					if(inMessage != null && inMessage.getData().containsKey("folder")){
						strSaveArrayData.add("folder");
						strSaveArrayData.add(Q(inMessage.getData().get("folder")));
					}
				}else{
					strSaveArrayData.add(field.getID());
					strSaveArrayData.add(field.getValue4JS());
					
				}
			}
		}
		
		if(optionalFields != null && optionalFields.size() > 0){
			for (IField field : optionalFields) {
				strSaveArrayData.add(field.getID());
				strSaveArrayData.add(field.getValue4JS());
			}			
		}
		
		String jsSaveData = jsData(strSaveArrayData
				.toArray(new String[0]));

		List<String> strCancelArrayData = new ArrayList<String>();
		if(inAppId != null){
			strCancelArrayData.add("appid");
			strCancelArrayData.add(Utils.Q(inAppId));
		}
		strCancelArrayData.add("handler");
		strCancelArrayData.add(Utils.Q(inCancelHandler));
		strCancelArrayData.add("action");
		strCancelArrayData.add(Utils.Q(inCancelAction));
		strCancelArrayData.add("dest");
		strCancelArrayData.add(Utils.Q(Utils.sanitazeHtmlId(inDest)));

		String jsCancelData = jsData(strCancelArrayData
				.toArray(new String[0]));

		StringBuffer ssJsActions = new StringBuffer();
		if(fileField == null){
			ssJsActions.append(Utils.T("template.html.a.onClick.sendMessage.Label"
					, jsSaveData
					, "Save"));
		}else{
			ssJsActions.append(Utils.T("template.html.a.onClick.sendMessage2.Label"
					, jsSaveData, fileField
					, "Save"));			
		}
		ssJsActions.append(" | ");
		ssJsActions.append(Utils.T("template.html.a.onClick.sendMessage.Label"
				, jsCancelData
				, "Cancel"));

		String jsActions = ssJsActions.toString();

		StringBuffer result = new StringBuffer(inTitle);

		result.append("<table class=\"statistics\">");
		result.append("<tbody>");
		if(fields != null){
			for (IField s : fields){
				if(s.isVisible()){
					result.append(String.format(
							"<tr><td class=\"tr\">%s:</td><td>%s</td></tr>"
							, String.format("[[Dictionary|Text|%s|span]]", s.getID())
							, s.getAsHtml()));
				}else{
					result.append(s.getAsHtml());
				}
			}
		}
		if(optionalFields != null){
			result.append("<tr><td colspan=\"2\"><u><i>[[Dictionary|Text|additional|span]]</i></u></td></tr>");			
			for(IField s :optionalFields)
				result.append(String.format(
						"<tr><td class=\"tr\">%s:</td><td>%s</td></tr>"
								, String.format("[[Dictionary|Text|%s|span]]", s.getID())
								, s.getAsHtml()));
		}
		result.append(String.format("<tr><td>&nbsp;</td><td>%s</td></tr>",
				jsActions));
		result.append("<tr><td>&nbsp;</td><td id='wait_element'>&nbsp;</td></tr>");
		result.append("</tbody>");
		result.append("</table>");

		return (result.toString());
	}

	public static boolean isFieldFileUploadType(IField s) {
		if(s == null) return false;
		if(s instanceof FieldInput)
			return( ((FieldInput) s).getType().compareToIgnoreCase("file") == 0 );
		return false;
	}
	
	public static void testFieldEMail(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String mailString, 
			String fieldId) {
		if(mailString == null || (!isValidEmailAddress(mailString))){
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_EMAIL);
			errorFields.add(fieldId);
		}
	}

	public static void testFieldKey(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes,
			String inValue, 
			String fieldId, 
			int inSizeMax) {
		if(inValue == null || inValue.isEmpty()){
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_KEY);
			errorFields.add(fieldId);
			return;
		}
		if(inValue.length() > inSizeMax){
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_SIZE);
			errorFields.add(fieldId);
			return;			
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
			result += String.format("[[Dictionary|Text|%s|NULL]]", t);
		}
		return(result);
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

	public static boolean errDBCodeValueExest(ERROR_CODES err) {
		boolean result = false;
		switch (err) {
		case NO_ERROR:
			result = true;			
			break;
		default:
			break;
		}
		return result;
	}

	public static String F(String format, Object...args) {
		return String.format(format, args);
	}

	public static String toogleLink(
			String divID, String title) {
		String format = "<a href=\"#\" title=\"Preview\" onclick=\"javascript:void(Globals.toogleBlock('%s')); return false;\">%s</a>";
		return(String.format(format, divID, title));
	}

	public static String escapeHtmlAndMyTags(String value) {
		if(value == null) return("");
		String result = StringEscapeUtils.escapeHtml(value);
		result = result.replaceAll("\\[\\[", "[");
		result = result.replaceAll("\\]\\]", "]");
		return (result);
	}
	
	public static void dumpIncomingWebMessage(IMessage inMessage) {
		System.out.println("=== Start ===");
		if(inMessage == null){
			System.out.println("=== NULL ===");
		}
		if(inMessage.getData() != null){
			System.out.println("DATA:");
			for(Entry<String, String> kv: inMessage.getData().entrySet()){
				System.out.println(F("%s: %s", kv.getKey(), kv.getValue()));
			}
		}
		System.out.println("inMessage.isReloadPage(): " + inMessage.isReloadPage());
		System.out.println("inMessage.getContextPath(): " + inMessage.getContextPath());
		System.out.println("inMessage.getError(): " + inMessage.getError());
		System.out.println("inMessage.getSessionID(): " + inMessage.getSessionId());
		System.out.println("inMessage.getUrl(): " + inMessage.getUrl());
		System.out.println("inMessage.getContextPath(): " + inMessage.getContextPath());
		System.out.println("=== End ===");
	}

	public static String sanitazeHtmlId(String string) {
		if(string == null) return null;
		return(string.replaceAll("\\W", "_"));
	}

	public static String replaceAll(String inString) {
		return(inString.replaceAll("(?i)</textarea>", "[[Dictionary|Template|template.textarea.endtag|html]]"));
	}

	public static void wrapElement(
			String inKey, 
			StringWrapper content, 
			String inHandlerName,
			String inEditObjectName,
			boolean noError, 
			Map<String, String> editLinks,
			String inWrapper) {
	
		String wrapElemId = sanitazeHtmlId(String.format("%s.%s", inEditObjectName, inKey));
		String wrapString = String.format("<%s id='%s'>%s</%s>", inWrapper, wrapElemId, content.getString(), inWrapper);
		content.setString(wrapString.toString());
		// List of Link
		if(editLinks != null && !editLinks.containsKey(wrapElemId)){
			StringBuffer result = new StringBuffer();
			
			// main link
			if(!noError)
				result.append("<a class='red' onclick=\"javascript:void(Globals.editIt('");
			else
				result.append("<a class='green' onclick=\"javascript:void(Globals.editIt('");
			result.append(inKey).append("','").append(inHandlerName).append("','").append("edit" + inEditObjectName)
						.append("')); return false;\" href=\"#\">").append(inKey).append("</a>");
			// sup - description
			result.append("<sup>(<a class='green' onclick=\"javascript:void(Globals.toogleVisibility('");
			result.append(wrapElemId).append("')); return false;\" href=\"#\">Show me</a>, ")
				.append(wrapString.toString().length()).append(")</sup>");
			
			editLinks.put(wrapElemId, result.toString());
		}
	}
}
