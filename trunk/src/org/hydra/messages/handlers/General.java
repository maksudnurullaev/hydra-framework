package org.hydra.messages.handlers;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;

public class General extends AMessageHandler { // NO_UCD
	public static final String _body_html_path_format = "/h/index_%s_%s.html";

	public IMessage getTextByKey(CommonMessage inMessage) {
		if(!testParameters(	inMessage, Constants._data_key)) return inMessage;			
		inMessage.setHtmlContent(MessagesManager.getText(
				inMessage.getData().get(Constants._data_key), 
				"div", 
				inMessage.getData().get(Constants._data_locale)));
			
		return inMessage;
	}	

	public IMessage changeLocale(CommonMessage inMessage){
		if(!testParameters(inMessage, Constants._data_key)) return inMessage;				
		getLog().debug("Try to change current locale to: " + inMessage.getData().get(Constants._data_key));
		
		// change session
		String new_locale = inMessage.getData().get(Constants._data_key);
		Result result = inMessage.set2HttpSession(Constants._data_locale, new_locale);
		// if something wrong		
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			return inMessage;				
		}
		getLog().debug("Locale sucessefully changed to: " + new_locale);
		// Change message locale too...
		inMessage._locale = new_locale;
		return getInitialHTMLElements(inMessage);
	}
	
	public IMessage getInitialHTMLElements(CommonMessage inMessage){
		getLog().debug("get stylesheets");
		inMessage.setStyleSheets(inMessage._web_application.getStylesheets());
		String path2File = String.format(_body_html_path_format, 
				inMessage._web_application.getId(),
				inMessage._locale);
		path2File = inMessage._web_context.getServletContext().getRealPath(path2File);
		getLog().debug("get html content from file: " + path2File);
		Result result = forwardToString(path2File);		
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
