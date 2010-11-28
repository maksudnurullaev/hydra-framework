package org.hydra.utils;

import java.util.regex.Pattern;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class Constants {
	// **** Beans
	public static final String _beans_main_input_pipe = "_main_input_pipe_";
	public static final String _beans_main_message_collector = "_main_message_collector";
	public static final String _beans_statistics_collector = "_statisticsCollector";
	// Cassandra beans
	public static final String _beans_cassandra_descriptor = "_cassandra_descriptor";
	public static final String _beans_cassandra_accessor = "_cassandra_accessor";
	// Applicatons desriber bean
	public static final String _beans_hydra_applications = "_hydra_applications";
	// **** Common constants
	static final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	static final String time_uid_format = "yyyy.MM.dd HH:mm:ss SSS";
	public static final int _unlimited = -1;
	public static final String UnknownString = "Unknown";
	// **** For dispatcher
	public static final String _message_handler_class_prefix = "org.hydra.messages.handlers.";
	public static final String _file_name_delimiter = "_";
	public static final String _beans_text_manager = "_text_manager";
	// **** Deafult response waiting time (in milliseconds)
	public static final long _max_response_wating_time = 5000;
	// **** Cassandra's constants
	public static final String _attention_error_str = "-!!!-!!!- ";
	public static final String KEY_COLUMNS_DEF = "COLUMNS";
	public static final String _unknown_web_application = "###ONKNOWN_WEB_APPLICATION###";
	// *** Result response type
	public static final String _data_what_error_message = "error.message";
	public static final String _data_what_html_content = "html.content";
	// **** Session data keys
	public static final String _data_locale = "locale";
	public static final String _data_sessionId = "sessionId";
	// **** Other default keys
	public static final String _data_what = "what";
	public static final String _data_key = "key";
	public static final String _data_value = "value";
	// **** Cassandra keys
	public static final String _data_cs_ksp = "cs_ksp";
	public static final String _data_cs_cf = "cs_cf";
	public static final String _data_cs_col = "cs_col";
	// **** Temporary keys
	public static final String _temp_value = "_temp_value";
	public static final String _url_server_port = "_url_server_port";
	public static final String _url_server_name = "_url_server_name";
	// *** URL definitions
	public static final String _url_scheme = "_url_scheme";
	public static final String _user_id = "_user_id";
	// *** General fields
	public static final String _handler_id = "handler";
	public static final String _action_id = "action";
	public static final String _app_id = "_app_id";
}
