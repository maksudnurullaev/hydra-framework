package org.hydra.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.MessagesManager;
import org.hydra.utils.Moder.MODE;

/**
 * @author M.Nurullayev
 */
public final class Utils {	
	private static final Log _log = LogFactory.getLog("org.hydra.utils.Utils");

	public static String wrap2HTMLTag(String inHTMLTagName, String inContent) {
		return String.format("<%s>%s</%s>", inHTMLTagName, inContent, inHTMLTagName);
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

	public static String trace(Object inObj, StackTraceElement[] stackTraceElements) {
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

	public static String makeJSLink(String inLabelName, String ...inStrings){
		if(inStrings == null || inStrings.length == 0) return "Invalid makeJSLink parameters!";
		String result = "";
		for(String name:inStrings){
			if(result.length() != 0) result += ",";
			result += name;
		}
		return String.format(MessagesManager.getTextManager().getTemplate("template.html.a.onClick.sendmessage.Label"),
												result,
												inLabelName);	
	}

	public static String GetDateUUID(){ // NO_UCD
		return Utils.GetCurrentDateTime(Constants.time_uid_format) + " - " + Utils.GetUUID();
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
			inResult.setObject(FileUtils.readFileToString(file, Constants._utf8));
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
			String inUserID, 
			Moder inModer) {
		return(inModer != null && (inModer.getMode() != MODE.MODE_UKNOWN));	
	}

	public static String shrinkString(String inString) {
		if(inString == null) return "NULL";
		inString = inString.trim();
		if(inString.length() > 10 )
			inString = inString.substring(0, 7) + "...";
		return inString;
	}

	public static String formatEditLinks(List<String> links) {
		if(links == null || links.size() == 0) return "";
		StringBuffer result = new StringBuffer("<div class=\"editlinks\">");
		for (String link : links) {
			if(result.length() != 0)	result.append(" ");
			result.append(link);
		}
		result.append("</div>");
		return result.toString();	
	}
}
