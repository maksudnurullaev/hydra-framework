package org.hydra.messages.handlers;

import java.util.HashMap;
import java.util.Map;

import org.hydra.deployers.ADeployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.CaptchaUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Utils;

public class ClientMessage extends AMessageHandler {
	static final String _cfName = "ClientMessage";
	
	public IMessage list(CommonMessage inMessage){		
		String content  = String.format("[[Application|ClientMessages|%s|%s]]", 
				inMessage.getData().get("appid"),
				inMessage.getData().get("dest"));
		getLog().debug("Try to get content for: " + content);
		
		return(ADeployer.deployContent(content,inMessage));
	}	
	
	public IMessage add(CommonMessage inMessage){		
		Utils.dump(inMessage);
		if((!AMessageHandler.validateData(inMessage, "text")) || 
				(!CaptchaUtils.validateCaptcha(inMessage))){
			return inMessage;
		}
		
		String appId = inMessage.getData().get("appid");
		String key = Utils.GetDateUUID();

		Map<String, String> message_entries = new HashMap<String, String>();
		
		for(Map.Entry<String, String> entry: inMessage.getData().entrySet()){
			if(entry.getKey().contains("text") && entry.getValue() != null ){
				if(entry.getValue().length() > Constants._max_client_msg_fields_length){
					getLog().warn(Utils.F("Message length limit: %s(%s)", 
							entry.getKey(),
							entry.getValue().length()));
					inMessage.setError(MessagesManager.getText("Error.Too.Long.Data", null, inMessage.getData().get("_locale")));
					return inMessage;
				}
				message_entries.put(entry.getKey(), entry.getValue());
			}
		}
		message_entries.put("tag", "Tag.New");		
		// save data
		for(Map.Entry<String, String> entry: message_entries.entrySet()){
			ErrorUtils.ERROR_CODES errorCode = DBUtils.setValue(appId, _cfName, key, entry.getKey(), entry.getValue());
			if(errorCode != ErrorUtils.ERROR_CODES.NO_ERROR){
				inMessage.setError("Error: " + errorCode);
				return inMessage;
			}
		}
		inMessage.setHtmlContent(MessagesManager.getText("MessageSaved", null, inMessage.getData().get("_locale")));
		return inMessage;
	}

	public IMessage delete(CommonMessage inMessage){
		if(!validateData(inMessage, "key")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String key = inMessage.getData().get("key");
				
		ErrorUtils.ERROR_CODES errCode = DBUtils.deleteKey(appId, _cfName, key);
		if(errCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(list(inMessage));
	}		
}
