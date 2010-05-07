package org.hydra.messages.handlers;

import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.MessagesManager;

public class MessageHandler extends AMessageHandler {
	public static final String _action = "action";
	public static final String _key = "key";
	public static final String _action_get_html_content = "get_html_content";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		
		if(!testParameters(inMessage, _action)) return inMessage;		
		
		// - Handle request message
		if(inMessage.getData().get(_action).equals(_action_get_html_content)){
			String locale = inMessage.getData().get(IMessage._data_locale);
			String textKey = inMessage.getData().get(_key);
			
			getLog().debug(String.format("Try to find text by key(%s) and locale(%s)", textKey, locale));
			inMessage.setHtmlContent(MessagesManager.getText(textKey, "div", locale));
		}else{
			String errorStr = String.format("Uknown action: " + inMessage.getData().get(_action));
			getLog().error(errorStr);
			inMessage.setError(errorStr);
		}
			
		return inMessage;
	}

}
