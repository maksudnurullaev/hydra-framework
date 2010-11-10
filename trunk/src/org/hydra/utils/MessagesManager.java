package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.text.TextManager;
import org.hydra.utils.abstracts.ALogger;
import org.springframework.web.context.WebApplicationContext;

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
		_log.debug("Try to get bean from springs");
		WebApplicationContext webContext = BeansUtils.getWebApplicationContext();
		if(webContext != null){
			_textManager = (TextManager) webContext.getBean(Constants._beans_text_manager);
			_log.debug("Found spring's default TextManager bean dictionary!");
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
	
	public static String getText(String inKey, String inHtmlWrap, String inLocale){
		if(!getTextManager().getLocales().containsKey(inLocale))
			return  "error.locale.not.found" ;
		
		if(inLocale == null)
			inLocale = getTextManager().getDefaultLocale();		
		
		_log.debug(String.format("Try to find text by key(%s) and locale(%s)", inKey, inLocale));
		return getTextManager().getTextByKey(inKey, inHtmlWrap, inLocale);
	}
		
	public static String getTemplate(String inKey){
		return getTextManager().getTemplate(inKey);
	}

}
