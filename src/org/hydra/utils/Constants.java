package org.hydra.utils;

import java.util.regex.Pattern;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class Constants {
	// **** Beans
	public static final String _bean_main_input_pipe = "_main_input_pipe_";
	public static final String _bean_main_message_collector = "_main_message_collector";
	public static final String _beans_statistics_collector = "_statisticsCollector";
	public static final String _bean_ksp_manager = "_ksp_manager";
	// Applicatons desriber bean
	public static final String _bean_hydra_web_applications = "_hydra_web_applications";
	public static final String _bean_web_app_id_postfix = "_WebAppBean";
	
	// **** Cassandra
	public static final String _cassandra_cluster = "_cassandra_cluster";
	
	// **** Common constants
	static final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	static final String time_uid_format = "yyyy.MM.dd HH:mm:ss SSS";
	public static final int _unlimited = -1;
	public static final String UnknownString = "Unknown";
	
	// **** For dispatcher
	public static final String _message_handler_class_prefix = "org.hydra.messages.handlers.";
	public static final String _file_name_delimiter = "_";
	public static final String _beans_text_manager = "_text_manager";
	
	// **** Deafult limits
	public static final int _max_textarea_field_limit = 4096;
	public static final int _max_input_field_limit = 128;
	public static final long _max_response_wating_time = 5000;
	
	// **** Message action keys
	public static final String _handler_id = "handler";
	public static final String _action_id = "action";
	
	// **** Session keys
	public static final String _session_locale = "locale";
	public static final String _session_user_id = "user_id";	
	
	// **** Constant templates
	public static final String _language_bar_a_template = "<a href=\"#\" onclick=\"javascript:void(Globals.sendMessage({handler:'General',action:'changeLocale', locale:'%s', dest:'body', url: document.URL })); return false;\">%s</a>";
	
	// **** Constant error strings
	public static final String _error_db = "ERROR_DB";
	
	// **** Constants for access modes
	public static final String _mode_edit_templates = "edit_templates";
	public static final String _utf8 = "UTF8";
	
	public static final String[] _GLOBAL_TAGS = {"User", "User.Editor", "User.Publisher", "User.Administrator"};
	public static final String _captcha_value = "CaptchaValue";
}
