package org.hydra.messages.handlers;

import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;

public class MessageHandler extends AMessageHandler {
	private static final String _data_what_html_content = "html.content";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// - Test incoming message
		if(!isValidMessage(inMessage)) return inMessage;
		
		// - Handle request message
		if(inMessage.getData().get(_what).equals(_data_what_html_content)){
			if(inMessage.getData().get(IMessage._string_locale) != null){
				inMessage.getData().put(IMessage._data_value, 
						MessagesManager.getTextManager().getTextByKey(inMessage.getData().get(_kind),inMessage.getData().get(IMessage._string_locale)));
			}else{
				inMessage.getData().put(IMessage._data_value, 
						MessagesManager.getTextManager().getTextByKey(inMessage.getData().get(_kind)));				
			}
		}else{
			getLog().error("error.unknown.message.type: " + inMessage.getData().get(_what));			
			inMessage.getData().put(IMessage._data_value, 
					MessagesManager.getTextManager().getTextByKey("error.unknown.message.type") + ": " + inMessage.getData().get(_kind));
		}
			
		return inMessage;
	}

}
