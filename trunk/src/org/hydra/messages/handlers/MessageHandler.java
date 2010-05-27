package org.hydra.messages.handlers;

import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.MessagesManager;

public class MessageHandler extends AMessageHandler {
	public static final String _action_get_html_content = "get_html_content";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		
		if(!testParameters(inMessage, IMessage._data_action)) return inMessage;		
		
		// - Handle request message
		if(inMessage.getData().get(IMessage._data_action).equals(_action_get_html_content)){
			if(!testParameters(inMessage, IMessage._data_key)) return inMessage;		
			String locale = inMessage.getData().get(IMessage._data_locale);
			String textKey = inMessage.getData().get(IMessage._data_key);
			
			inMessage.setHtmlContent(MessagesManager.getText(textKey, "div", locale));
		}else{
			String errorStr = String.format("Uknown action: " + inMessage.getData().get(IMessage._data_action));
			getLog().error(errorStr);
			inMessage.setError(errorStr);
		}
			
		return inMessage;
	}

}
