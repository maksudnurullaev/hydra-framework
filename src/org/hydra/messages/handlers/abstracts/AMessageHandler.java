package org.hydra.messages.handlers.abstracts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.intefaces.IMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.abstracts.ALogger;

public abstract class AMessageHandler extends ALogger implements IMessageHandler {
	private static Log _log = LogFactory.getLog("org.hydra.messages.handlers.abstracts.AMessageHandler");	
	public static boolean validateData(IMessage inMessage, String ...keys){
		if(inMessage.getData() == null ){
			_log.error("ERROR: data is null!");
			inMessage.setError("ERROR: data is null!");
			return false;
		}
		for(String key:keys){
			if(!inMessage.getData().containsKey(key)){
				_log.error("ERROR: No key: " + key);
				inMessage.setError("ERROR: No key: " + key);
				return false;
			}
		}
		return true;		
	}
	public static boolean validateFile(CommonMessage inMessage) {
		if(inMessage.file == null
				|| inMessage.file.getSize() == 0){
			return false;						
		}
		return true;
	}	
}
