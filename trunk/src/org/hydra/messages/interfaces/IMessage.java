package org.hydra.messages.interfaces;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.hydra.utils.Result;

public interface IMessage {
	// *** General fields
	static final String _handler_id = "handler";		
	static final String _action_id = "action";
	
	// *** Result response type
	static final String _data_what_error_message = "error.message" ;
	static final String _data_what_html_content  = "html.content" ;
	
	// **** Session data keys
	static final String _data_locale = "locale";
	static final String _data_userId = "userId";
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
	
	void setData(Map<String, String> _data);
	Map<String, String> getData();
	
	void setError(String inErrorMessage);
	
	void setHtmlContent(String inHtmlContent);
	
	Result setToHttpSession(String inKey, Object inObj);
	void setHttpSession(HttpSession inSession);
	HttpSession withHttpSession();
		
}