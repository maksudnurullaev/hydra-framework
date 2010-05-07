package org.hydra.messages.handlers.abstracts;

import java.util.Map;

import org.hydra.messages.MessageBean;
import org.hydra.messages.handlers.intefaces.IMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.abstracts.ALogger;

public abstract class AMessageHandler extends ALogger implements IMessageHandler {
	
	public static final String[]defaultFields = {"handler"};
	
	public static final boolean isValidData(Map<String, String> inMap, String[] inKeys){
		if(inMap == null) return false;
		
		// check Default
		for (String keyValue:defaultFields) {
			if(!inMap.containsKey(keyValue) ||
					inMap.get(keyValue) == null) return false;
		}
		
		// check additional
		for (String keyValue:inKeys) {
			if(!inMap.containsKey(keyValue) ||
					inMap.get(keyValue) == null) return false;
		}
		
		// Finish, all test passed
		return true;
	}		
	
	public boolean testParameters(IMessage inMessage, String ...keys){
		if(!(inMessage instanceof MessageBean)){
			String errorStr = "Unexpected message class: " + inMessage.getClass().getSimpleName();
			getLog().error(errorStr);
			inMessage.setError(errorStr);
			return false;
		}else if(!isValidData(inMessage.getData(), keys)){
			String errorStr = "Error! Parameter(s) missing:\n";
			int count  = 0;
			for(String key:keys){
				if(count++ > 0)
					errorStr += " or ";
				errorStr += key;
			}
			getLog().error(errorStr);
			inMessage.setError(errorStr);
			return false;			
		}			
		// Finish, all test passed
		return true;		
	}
	
}
