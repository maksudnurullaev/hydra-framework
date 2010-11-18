package org.hydra.messages.interfaces;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.hydra.utils.Result;

public interface IMessage {
	static final String _app_id = "_app_id";
	// *** General fields
	static final String _handler_id = "handler";		
	static final String _action_id = "action";
	static final String _user_id = "_user_id";
	// *** URL definitions
	static final String _url_scheme = "_url_scheme";
	static final String _url_server_name = "_url_server_name";
	static final String _url_server_port = "_url_server_port";		
	
	// *** Result response type
	static final String _data_what_error_message = "error.message" ;
	static final String _data_what_html_content  = "html.content" ;
	
	// **** Session data keys
	static final String _data_locale = "locale";
	static final String _data_sessionId = "sessionId";
		
	// **** Other default keys
	static final String _data_what  = "what" ;
	static final String _data_key  = "key" ;
	static final String _data_value  = "value" ;
	
	// **** Cassandra keys
	static final String _data_cs_ksp   = "cs_ksp";
	static final String _data_cs_cf    = "cs_cf";
	static final String _data_cs_key   = "cs_key";
	static final String _data_cs_col   = "cs_col";
	
	// **** Temporary keys
	static final String _temp_value = "_temp_value";
		
	void setError(String inErrorMessage);
	
	void setHtmlContent(String inHtmlContent);
	
	Result setToHttpSession(String inKey, Object inObj);
	void setHttpSession(HttpSession inSession);
	void setHttpSession2(HttpSession httpSession);
	void setRealPath(String path2File, String inDataKey);
	void setServerInfo(String inDataKey);

	void setData(Map<String, String> data);
	Map<String, String> getData();

	void setStyleSheets(Set<String> stylesheets);		
}