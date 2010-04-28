package org.hydra.messages.handlers.abstracts;

import java.util.Map;

import org.hydra.messages.MessageBean;
import org.hydra.messages.handlers.intefaces.IMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.abstracts.ALogger;

public abstract class AMessageHandler extends ALogger implements IMessageHandler {
	/**
	 * Unique data key to request message ID
	 */
	protected static final String _what = "what";
	protected static final String _kind = "kind";
	protected static final String[] _mansatory_data_keys = {_what, _kind};
		
	
	public static final boolean isValidData(Map<String, String> inMap, String[] inKeys){
		if(inMap == null) return false;
		
		// Check mandatories
		for (String keyValue:_mansatory_data_keys) {
			if(!inMap.containsKey(keyValue) ||
					inMap.get(keyValue) == null) return false;
		}
		
		// Check additional
		for (String keyValue:inKeys) {
			if(!inMap.containsKey(keyValue) ||
					inMap.get(keyValue) == null) return false;
		}
		
		// Finish, all test passed
		return true;
	}		
	
	public boolean isValidMessage(IMessage inMessage, String ...keys){
		if(!(inMessage instanceof MessageBean)){
			getLog().error("Unexpected message class: " + inMessage.getClass().getSimpleName());
			inMessage.setError("Unexpected message class: " + inMessage.getClass().getSimpleName());
			return false;
		}else if(!isValidData(inMessage.getData(), keys)){
			getLog().error("Incoming message does not have necessary data");
			inMessage.setError("Incoming message does not have necessary data");
			return false;			
		}			
		// Finish, all test passed
		return true;		
	}
	
}
