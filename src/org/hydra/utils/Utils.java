package org.hydra.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.managers.MessagesManager;
import org.hydra.managers.TextManager;

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

	/* **** Content Deployment **** */
	public static String deployContent(
			String htmlContent, 
			String inApplicationID, 
			String inLocale, 
			String inUserID) {
		String patternStr = "\\[\\[(\\S+)\\|(\\S+)\\|(\\S+)\\|(\\S+)\\]\\]";

		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(htmlContent);

		StringBuffer buf = new StringBuffer();
		while ((matcher.find())) {
			_log.debug("WHERE: " + matcher.group(1));
			_log.debug("FROM: " + matcher.group(2));
			_log.debug("KEY: " + matcher.group(3));
			_log.debug("HOW: " + matcher.group(4));
			_log.debug("ApplicationID: " + inApplicationID);
			_log.debug("Locale: " + inLocale);
			_log.debug("UserID: " + inUserID);
			
			matcher.appendReplacement(buf,  
					getWhereWhatKeyHow(
							matcher.group(1),  // WHERE
							matcher.group(2),  // WHAT
							matcher.group(3),  // KEY
							matcher.group(4),  // HOW
							inApplicationID, 
							inLocale,
							inUserID)
					);
		}
		matcher.appendTail(buf);
		return(buf.toString());
	}

	private static String getWhereWhatKeyHow(
			String inWhere, 
			String inWhat,
			String inKey, 
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID
			) {
		if(inWhere.compareToIgnoreCase("db") == 0)
			return getDbWhatKeyHow(inWhat, inKey, inHow, inApplicationID, inLocale, inUserID);
		else if(inWhere.compareToIgnoreCase("system") == 0)
			return getSystemWhatKeyHow(inWhat, inKey, inHow, inLocale);
		return "Could not find WHERE part: " + inWhere ;
	}

	private static String getSystemWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow, 
			String inLocale) {
		if(inWhat.compareToIgnoreCase("LanguageBar") == 0)
			return getSystemLanguagebarKeyHow(inKey, inHow, inLocale);
		return "Could not find WHERE part: " + inWhat;
	}

	private static String getSystemLanguagebarKeyHow(
			String inKey, // IGNORE 
			String inHow, 
			String inLocale) {
		if(inHow.compareToIgnoreCase("a") == 0) // HTML <a>...</a>
			return getSystemLanguagebarKeyA(inKey, inLocale);
		return "Could not find HOW part: " + inHow;
	}

	private static String getSystemLanguagebarKeyA(
			String inKey, // IGNORE 
			String inLocale) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._beans_text_manager);
		if(result.isOk() && result.getObject() instanceof TextManager){ // generate language bar
			TextManager tm = (TextManager) result.getObject();
			String resultStr = "";
			for (Map.Entry<String, String> entry:tm.getLocales().entrySet()) {
				if(entry.getKey().compareToIgnoreCase(inLocale) == 0){ // selected
					resultStr += entry.getValue();
				}else{
					resultStr += String.format(Constants._language_bar_a_template, entry.getKey(), entry.getValue());
				}
				if(!resultStr.isEmpty())
					resultStr += "&nbsp;&nbsp;";
			}
			return resultStr;
		}
		return "Could not find TextManager instance!";
	}

	private static String getDbWhatKeyHow(
			String inWhat, 
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID
			) {
		if(inWhat.compareToIgnoreCase("text") == 0)
			return getDbTextKeyHow(inKey, inHow, inApplicationID, inLocale, inUserID);
		return "Could not find What part : " + inWhat ;
	}

	private static String getDbTextKeyHow(
			String inKey,
			String inHow,
			String inApplicationID, 
			String inLocale,
			String inUserID
			){
		if(inHow.compareToIgnoreCase("div") == 0)
			return getDbTextKeyDiv(inKey, inApplicationID, inLocale, inUserID);
		return "Could not find HOW part : " + inHow ;
	};
	
	private static String getDbTextKeyDiv(
			String inKey,
			String inApplicationID, 
			String inLocale,
			String inUserID
			) {
		Result result = DBUtils.getFromKey("Text", inKey, inApplicationID, inLocale);
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("<sup>");
		resultBuffer.append(" <a onclick=\"javascript:void(Globals.editIt('").append(inKey).append("')); return false;\" href=\"#\">Edit</a>");
		resultBuffer.append(" | <a onclick=\"javascript:void(Globals.viewIt('").append(inKey).append("')); return false;\" href=\"#\">View</a>");
		resultBuffer.append(" | <a onclick=\"javascript:void(Globals.uploadIt('").append(inKey).append("')); return false;\" href=\"#\">Upload</a>");
		resultBuffer.append("</sup>");
		resultBuffer.append("<div id='").append(inKey).append("'>")
			.append(result.isOk()?result.getObject():String.format("[[DB|Text|%s|div]]",inKey))
			.append("</div>");
		return resultBuffer.toString();
	}

}
