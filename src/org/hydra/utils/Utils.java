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

	public static String GetDateUUID() { // NO_UCD
		return(String.format("%s %s"
				, Utils.GetCurrentDateTime(Constants.time_uid_format)
				, Utils.GetUUID().substring(0, 2)));
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
	
	public static boolean test4ValidPasswords(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			CommonMessage inMessage, 
			String key, 
			String key2) {
		String value = Utils.getMessageDataOrNull(inMessage, Constants._user_password_new1);
		String value2 = Utils.getMessageDataOrNull(inMessage, Constants._user_password_new2);
		
		if(value == null 
				|| value2 == null
				|| !value.trim().equals(value2.trim()) 
				|| (value.length() < 5)){
			errorFields.add(key);
			errorFields.add(key2);
			errorCodes.add(ERROR_CODES.ERROR_NO_VALID_PASSWORDS);
			return(false);
		}
		return(true);
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
				System.out.println(F("... %s: %s", kv.getKey(), kv.getValue()));
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

	public static String getMessageDataOrNull(IMessage inMessage,  String inKey) {
		return((inMessage.getData() != null ?  inMessage.getData().get(inKey) : null));
	}
}
