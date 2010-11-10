package org.hydra.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class Constants {
	// **** Project values
	public static final String _project_name = "Hydra";
	public static final String _project_version = "0.1";
	public static final String _conf_dir_location = "conf";
	public static final String _logs_dir_location = "logs";
	public static final String _date_time_id_format = "yyyy.MM.dd HH:mm:ss";
	
	// **** Beans
	public static final String _beans_main_input_pipe = "_main_input_pipe_";
	public static final String _beans_main_message_collector = "_main_message_collector";
	public static final String _beans_statistics_collector = "_statisticsCollector";
	// Cassandra beans
	public static final String _beans_cassandra_descriptor = "_cassandra_descriptor";
	public static final String _beans_cassandra_accessor   = "_cassandra_accessor";

	// **** Debug mode beans
	public static final String _debug_mode = "debug_mode";
	// **** Common constants
	public static final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

	public static final int _unlimited = -1;

	public static final String UnknownString = "Unknown";
	
	// **** For dispatcher
	public static final String _message_handler_class_posfix = "Handler";
	public static final String _message_handler_class_prefix = "org.hydra.messages.handlers.";
	public static final String _file_name_delimiter = "_";
	public static final String _beans_text_manager = "_text_manager";
	//public static final String _path2ApplicationContext_xml = "/applicationContext.xml";
	
	// **** Deafult response waiting time (in milliseconds)
	public static final long _max_response_wating_time = 3000;
	
	// **** Cassandra's constants
	public static final String _cassandra_descriptor_name = "_cassandra_server_descriptor";
	public static final String _attention_str = "-!!!-!!!- ";
	public static final String KEY_COLUMNS_DEF = "COLUMNS";	

	public static String GetCurrentDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(_date_time_id_format);
		return String.format(sdf.format(new Date()));
	};

	public static String GetCurrentDateTime(String inFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
		return sdf.format(new Date());
	};	
	
	public static String GetUUID() {
		return java.util.UUID.randomUUID().toString();

	}
	
	public static String GetDateUUID(){
		return Constants.GetCurrentDateTime("yyyy.MM.dd HH:mm:ss SSS") + " - " + Constants.GetUUID();
	}

	public static String GetDateUUIDTEST(){
		return "TEST - " + Constants.GetCurrentDateTime("yyyy.MM.dd HH:mm:ss SSS") + " - " + Constants.GetUUID();
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
	public static boolean isValidEmailAddress(String aEmailAddress) {
		Matcher m = p.matcher(aEmailAddress);
		return m.matches();
	}
	
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private static final int DefaultStringTrimLength = 50;
	private static final int DefaultStringTrimLengthGap = 10;
	// For JavaSctipt interchange
	public static final int FALSE = 0;
	public static final int TRUE = 1;

	public static String currentDateTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
	
	public static String trimString(String inString) {
		return trimString(inString, DefaultStringTrimLength,
				DefaultStringTrimLengthGap);
	}

	public static String trimString(String inString, int inMaxCount, int inGap) {
		// Initial check
		if (inMaxCount <= 0)
			inMaxCount = DefaultStringTrimLength;
		if (inGap <= 0)
			inGap = DefaultStringTrimLengthGap;
		if (inString.length() > (inMaxCount + inGap))
			return inString.substring(0, inMaxCount).trim() + "...";
		return inString;
	}

	public static Date getDate() {
		return (new Date());
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
	
	public static String makeJSLink(String inLabelName, String format, Object ...inObjects){
		return String.format(MessagesManager.getTextManager().getTemplate("template.html.a.onClick.sendmessage.Label"),
												String.format(format, inObjects),
												inLabelName);	
	}
}
