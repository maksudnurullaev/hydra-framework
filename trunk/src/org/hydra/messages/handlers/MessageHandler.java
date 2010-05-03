package org.hydra.messages.handlers;

import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.MessagesManager;

public class MessageHandler extends AMessageHandler {
	public static final String _what = "what";
	public static final String _kind = "kind";

	public static final String _what_html_content = "html.content";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// - Test incoming message
		if(!isValidMessage(inMessage)) return inMessage;
		
		// - Handle request message
		if(inMessage.getData().get(_what).equals(_what_html_content)){
			String locale = inMessage.getData().get(IMessage._data_locale);
			String textKey = inMessage.getData().get(_kind);
			
			getLog().debug(String.format("Try to find text by key(%s) and locale(%s)", textKey, locale));
			inMessage.setHtmlContent(MessagesManager.getText(textKey, "div", locale));
		}else{
			String errorStr = String.format("Uknown message what: " + inMessage.getData().get(_what));
			getLog().error(errorStr);
			inMessage.setError(errorStr);
		}
			
		return inMessage;
	}

}
