package org.hydra.messages.handlers;

import java.util.Map;

import org.hydra.deployers.ADeployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Utils;

public class ClientMessage extends AMessageHandler {
	static final String _cfName = "ClientMessage";
	
	public IMessage list(CommonMessage inMessage){		
		String content  = String.format("[[Application|ClientMessages|%s|%s]]", 
				inMessage.getData().get("_appid"),
				inMessage.getData().get("dest"));
		getLog().debug("Try to get content for: " + content);
		
		return(ADeployer.deployContent(content,inMessage));
	}	
	
	public IMessage add(CommonMessage inMessage){		
		if(inMessage.getData().isEmpty()){
			inMessage.setError(MessagesManager.getText("NoData", null, inMessage.getData().get("_locale")));
			return inMessage;
		}
		
		String appId = inMessage.getData().get("_appid");
		String key = Utils.GetDateUUID();
		// test data
		for(Map.Entry<String, String> entry: inMessage.getData().entrySet()){
			if(!isValidDataKey(entry.getKey())) continue;
			if(entry.getValue().length() > 1024){
				inMessage.clearContent();
				inMessage.setError(MessagesManager.getText("Error.Too.Long.Data", null, inMessage.getData().get("_locale")));
				return inMessage;
			}
		}
		// save data
		for(Map.Entry<String, String> entry: inMessage.getData().entrySet()){
			if(!isValidDataKey(entry.getKey())) continue;
			ErrorUtils.ERROR_CODES errorCode = DBUtils.setValue(appId, _cfName, key, entry.getKey(), entry.getValue());
			if(errorCode != ErrorUtils.ERROR_CODES.NO_ERROR){
				inMessage.setError("Error: " + errorCode);
				return inMessage;
			}
		}
		ErrorUtils.ERROR_CODES errorCode = DBUtils.setValue(appId, _cfName, key, "tag", "Tag.New");
		if(errorCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError("Error: " + errorCode);
			return inMessage;
		}

		inMessage.setHtmlContent(MessagesManager.getText("MessageSaved", null, inMessage.getData().get("_locale")));
		return inMessage;
	}

	private static boolean isValidDataKey(String key) {
		if(key == null) return false;
		if(key.compareToIgnoreCase("handler") == 0) return false;
		if(key.compareToIgnoreCase("action") == 0) return false;
		if(key.compareToIgnoreCase("dest") == 0) return false;
		return true;
	}
	
	public IMessage delete(CommonMessage inMessage){
		if(!validateData(inMessage, "_key")) return inMessage;
		String appId = inMessage.getData().get("_appid");
		String key = inMessage.getData().get("_key");
				
		ErrorUtils.ERROR_CODES errCode = DBUtils.deleteKey(appId, _cfName, key);
		if(errCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(list(inMessage));
	}		
}
