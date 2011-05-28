package org.hydra.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
			String inUserID,
			Moder inModer) {
		return (inModer != null && (inModer.getMode() != MODE.MODE_UKNOWN));
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
			return "";
		// StringBuffer result = new StringBuffer("<div class=\"editlinks\">");
		StringBuffer result = new StringBuffer();
		for (String link : links) {
			if (result.length() != 0)
				result.append(" ");
			result.append(link);
		}
		// result.append("</div>");
		return result.toString();
	}

	public static String createJSLinkHAKD(
			String inHandler
			, String inMethod
			, String inKey
			, String inDest
			, String inName
			) {
		String jsData = Utils.T("template.html.js.HAKD"
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

	public static String Q(String inString) {
		return ("'" + inString + "'");
	}

	public static String QQ(String inString) {
		return ("\"" + inString + "\"");
	}

	public static String getJSDataArray(String... strings) {
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
			String inAppId,
			String inSaveHandler, String inSaveAction, // Save
			String inCancelHandler, String inCancelAction, // Cancel
			String inDest,
			ArrayList<IField> fields) {

		List<String> strSaveArrayData = new ArrayList<String>();
		strSaveArrayData.add("appid");
		strSaveArrayData.add(Utils.Q(inAppId));
		strSaveArrayData.add("handler");
		strSaveArrayData.add(Utils.Q(inSaveHandler));
		strSaveArrayData.add("action");
		strSaveArrayData.add(Utils.Q(inSaveAction));
		strSaveArrayData.add("dest");
		strSaveArrayData.add(Utils.Q(inDest));
		for (IField s : fields) {
			strSaveArrayData.add(s.getID());
			strSaveArrayData.add(s.getValue4JS());
		}
		String jsSaveData = getJSDataArray(strSaveArrayData
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

		String jsCancelData = getJSDataArray(strCancelArrayData
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

		StringBuffer result = new StringBuffer();

		result.append("<table class=\"statistics\">");
		result.append("<tbody>");
		for (IField s : fields)
			result.append(String.format(
					"<tr><td class=\"tr\">%s:</td><td>%s</td></tr>"
							, String.format("[[DB|Text|%s|locale]]", s.getID())
							, s.getAsHtml()));
		result.append(String.format("<tr><td>&nbsp;</td><td>%s</td></tr>",
				jsActions));
		result.append("</tbody>");
		result.append("</table>");

		return (result.toString());
	}
}
