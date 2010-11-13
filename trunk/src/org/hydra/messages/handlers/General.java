package org.hydra.messages.handlers;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;

public class General extends AMessageHandler { // NO_UCD
	public static final String _body_html_path_format = "/h/body_%s.html";

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
		Result result = inMessage.setToHttpSession(IMessage._data_locale, inMessage.getData().get(IMessage._data_key));
		
		// 2. If something wrong		
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			return inMessage;				
		}else{ // 3. Change message attached locale
			inMessage.getData().put(IMessage._data_locale, 
					(String) inMessage.getHttpSession().getAttribute(IMessage._data_locale));
		}		
		return getInitialHTMLBody(inMessage);
	}
	public IMessage getInitialHTMLBody(IMessage inMessage){
		// - Get localed html templates
		String content = "";
		String locale = inMessage.getData().get(IMessage._data_locale);
		
		if(locale == null) locale = MessagesManager.getTextManager().getDefaultLocale();
		
		String path2File = String.format(_body_html_path_format, locale);
		
		getLog().debug("Get content of: " + path2File);
		content = forwardToString(path2File, inMessage);
		
		inMessage.setHtmlContent(content);				
		return inMessage;
	}	
	public String forwardToString(String inPath2HTMLTemplate, IMessage inMessage) {
		
		if(inMessage.getHttpSession() == null)
			return "Session does not attached to message!";
		
		String result = "Internale error!";
		
		result = inMessage.getHttpSession().getServletContext().getRealPath(inPath2HTMLTemplate);

		try {
			File file = new File(result);
			result = FileUtils.readFileToString(file, DBUtils._utf8_encoding);
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		
		return result;
	}	
	
}
