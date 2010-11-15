package org.hydra.messages.handlers;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;

public class General extends AMessageHandler { // NO_UCD
	public static final String _body_html_path_format = "/h/index_%s_%s.html";

	public IMessage getTextByKey(IMessage inMessage) {
		
		if(!testParameters(inMessage, "key")) return inMessage;		
		
		String locale = inMessage.getData().get(IMessage._data_locale);
		String textKey = inMessage.getData().get(IMessage._data_key);			
		inMessage.setHtmlContent(MessagesManager.getText(textKey, "div", locale));
			
		return inMessage;
	}	
	public IMessage changeLocale(IMessage inMessage){
		if(!testParameters(inMessage, "key")) return inMessage;		
		
		getLog().debug("Try to change current locale to: " + inMessage.getData().get(IMessage._data_key));
		
		// 1. Change session
		String changed_locale = inMessage.getData().get(IMessage._data_key);
		Result result = inMessage.setToHttpSession(IMessage._data_locale, changed_locale);
		
		// 2. If something wrong		
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			return inMessage;				
		}
		// 3. Change message locale too...
		inMessage.getData().put(IMessage._data_locale, changed_locale);
		return getInitialHTMLBody(inMessage);
	}
	public IMessage getInitialHTMLBody(IMessage inMessage){
		// - Get locale html templates
		String locale = inMessage.getData().get(IMessage._data_locale);
		
		if(locale == null) locale = MessagesManager.getTextManager().getDefaultLocale();
		
		String path2File = String.format(_body_html_path_format,
				inMessage.getData().get(IMessage._app_id),
				locale);
		
		inMessage.setRealPath(path2File, IMessage._temp_value);
		getLog().debug("Try to get content of: " + inMessage.getData().get(IMessage._temp_value));
		
		Result result = forwardToString(inMessage.getData().get(IMessage._temp_value));		
		if(result.isOk()) inMessage.setHtmlContent(result.getResult());
		else inMessage.setError(result.getResult());
		
		return inMessage;
	}		
	public Result forwardToString(String inPath2File) {
		Result result = new Result();
		try {
			File file = new File(inPath2File);
			result.setResult(FileUtils.readFileToString(file, DBUtils._utf8_encoding));
			result.setResult(true);
		} catch (Exception e) {
			_log.error(e.toString());
			result.setResult("Internal server error!");
			result.setResult(false);
		}
		return result;
	}	
}
