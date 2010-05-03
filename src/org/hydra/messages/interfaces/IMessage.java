package org.hydra.messages.interfaces;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.hydra.utils.Result;

public interface IMessage {
	// **** Session data keys
	static final String _data_locale = "locale";
	static final String _data_userId = "userId";
	static final String _data_sessionId = "sessionId";
		
	// **** Other default keys
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
	
	Result setToHttpSession(String inKey, Object inObj);
	void setHttpSession(HttpSession inSession);
	HttpSession withHttpSession();
		
}