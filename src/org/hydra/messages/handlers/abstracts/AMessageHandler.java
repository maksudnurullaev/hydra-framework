package org.hydra.messages.handlers.abstracts;

import java.util.ArrayList;
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
		
	@Override
	public boolean isValidMessage(IMessage inMessage) {
		if(!(inMessage instanceof MessageBean)){
			getLog().error("Unexpected message class: " + inMessage.getClass().getSimpleName());
			inMessage.setError("Unexpected message class: " + inMessage.getClass().getSimpleName());
			return false;
		}else if(!isValidData(inMessage.getData(), getMandatoryDataKeys())){
			getLog().error("Incoming message does not have necessary data");
			inMessage.setError("Incoming message does not have necessary data");
			return false;			
		}			
		// Finish, all test passed
		return true;
	}	
	
	@Override
	public String[] getMandatoryDataKeys() {
		if(getAdditionalManatoryDataKeys() == null) return _mansatory_data_keys;
		ArrayList<String> result = new ArrayList<String>();
		
		// Add default mandatory keys
		for(String elem:_mansatory_data_keys)
			result.add(elem);
		
		// Add addiotional mandatoryn keys
		for(String elem:getAdditionalManatoryDataKeys())
			result.add(elem);
		
		return (String[]) result.toArray();
		
	}

	public static final boolean isValidData(Map<String, String> map, String[] mandarotyDataKeys){
		if(map == null) return false;
		
		for (String keyValue:mandarotyDataKeys) {
			if(!map.containsKey(keyValue) ||
					map.get(keyValue) == null) return false;
		}
		// Finish, all test passed
		return true;
	}	
	
	@Override
	public String[] getAdditionalManatoryDataKeys() {
		return null;
	}
	
}
