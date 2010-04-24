package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.text.TextManager;

public final class MessagesManager{
	private static Log _log = LogFactory.getLog("org.hydra.utils.MessagesManager");	
	private static TextManager _textManager = null;	
	
	private static MessagesManager _messageMananger = null; 
	
	public static MessagesManager GetInstance(){
		if(_messageMananger == null)
			_messageMananger = new MessagesManager();
		return _messageMananger;
	}
	
	public MessagesManager(){
		// 1. Try to get bean from springs
		if(SessionManager.getWebApplicationContext() != null){
			_textManager = (TextManager) SessionManager.getWebApplicationContext().getBean(Constants._beans_text_manager);
			_log.debug("Found spring's default bean dictionary: " + _textManager.getFileName());
		// 2. If not 
		}else{
			_textManager = new TextManager();
			_log.debug("Create local TextManager instance, not Spring's one!");
		}
	}
	
	public static TextManager getTextManager() {
		if(GetInstance() != null)
			return _textManager;
		
		_log.error("Could not intiliaze MessageManager");
		
		return null;
	}
	
}
