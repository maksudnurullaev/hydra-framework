package org.hydra.managers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;

public final class MessagesManager{
	private static Log _log = LogFactory.getLog("org.hydra.managers.MessagesManager");	
	private static TextManager _textManager = null;	
	
	private static MessagesManager _messageMananger = null; 
	
	private static MessagesManager GetInstance(){
		if(_messageMananger == null)
			_messageMananger = new MessagesManager();
		return _messageMananger;
	}
	
	public MessagesManager(){
		Result result = new Result();
		_log.debug("Try to get bean from springs");
		BeansUtils.getWebContextBean(result, Constants._beans_text_manager);
		if(result.isOk() && result.getObject() instanceof TextManager){
			_textManager = (TextManager) result.getObject();
			_log.debug("Found spring's default TextManager bean dictionary!");
		}else{
			_textManager = new TextManager();
			_log.warn("Create local TextManager instance, not Spring's one!");
		}
	}
	
	public static TextManager getTextManager() {
		if(GetInstance() != null)
			return _textManager;
		
		_log.error("Could not intiliaze MessageManager");
		
		return null;
	}
	
	public static String getText(String inKey, String inHtmlWrap, String inLocale){
		if(inKey == null) { return("Error: send NULL instead some key!");}
		if(inLocale == null)
			inLocale = getTextManager().getDefaultLocale();		
		
		_log.debug(String.format("Find text by key/locale: %s/%s", inKey, inLocale));
		
		return getTextManager().getTextByKey(inKey, inHtmlWrap, inLocale);
	}
		
	public static String getTemplate(String inKey){
		if(getTextManager() != null)
			return getTextManager().getTemplate(inKey);
		return "ERROR: Could not intiliaze MessageManager!";
	}

}
