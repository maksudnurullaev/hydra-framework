package org.hydra.messages.handlers.abstracts;

import org.hydra.messages.handlers.intefaces.IMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.abstracts.ALogger;

public abstract class AMessageHandler extends ALogger implements IMessageHandler {
	public static boolean validateData(IMessage inMessage, String ...keys){
		if(inMessage.getData() == null ) return false;
		for(String key:keys){
			if(!inMessage.getData().containsKey(key)){
				inMessage.setError("ERROR: No key: " + key);
				return false;
			}
		}
		return true;		
	}
	
}
