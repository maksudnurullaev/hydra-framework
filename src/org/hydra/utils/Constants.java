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
	public static final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	public static final String time_uid_format = "yyyy.MM.dd HH:mm:ss SSS";
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
	public static final long _max_client_msg_fields_length = 1024;
		
	// **** Constant error strings
	public static final String _error_db = "ERROR_DB";
	
	// **** Constants for access modes
	public static final String _mode_edit_templates = "edit_templates";
	public static final String _utf_8 = "UTF-8";

	// **** other values
	public static final String _captcha_value = "captcha_value";
	public static final String _captcha_OK = "_captcha_OK";
	public static final String _admin_app_action_div = "admin_app_action_div";
	public static final String _admin_app_sub_action_div = "admin_app_sub_action_div";
	public static final String _url_mode_param = "mode=";
	public static final String _roles_key = "_roles";
	public static final String _userid_key = "_userid";
	public static final String _browser_key = "browser";
	public static final String _locale_key = "locale";
	public static final String _appid_key = "appid";
	public static final String _content_key = "content";
	public static final String _key = "key";
	public static final String _div = "div";
	public static final String _folder = "folder";
	public static final String _file_path = "file_path";
	public static final String _file_real_path = "file_real_path";
	public static final String _user_password = "user_password";
	public static final String _user_password_new1 = "user_password_new1";
	public static final String _user_password_new2 = "user_password_new2";
}
