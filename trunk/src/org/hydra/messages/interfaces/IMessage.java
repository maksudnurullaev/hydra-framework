package org.hydra.messages.interfaces;

import java.util.Map;

public interface IMessage {
	// **** Inrernal session data
	static final String _string_locale = "locale";
	static final String _string_userId = "userId";
	
	// **** Default data types
	static final String _data_sessionId = "sessionId";
	static final String _data_handler = "handler";
	
	static final String _data_what = "what" ;
	static final String _data_kind = "kind" ;
	static final String _data_value = "value" ;
	
	static final String _data_what_error_message = "error.message" ;
	static final String _data_what_html_content  = "html.content" ;	

	void setData(Map<String, String> _data);
	Map<String, String> getData();
	
	void setError(String inErrorMessage);
	void setHtmlContent(String inHtmlContent);
}