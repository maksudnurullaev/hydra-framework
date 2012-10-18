package org.hydra.messages.handlers;

import java.util.List;

import org.hydra.deployers.ADeployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

public class Tagger extends AMessageHandler {

	public IMessage add(CommonMessage inMessage){
		if (!validateData(inMessage, "appid", "elemid", "value", "addvalue", "prefixes", "dest"))
			return inMessage;
		
		String appId = inMessage.getData().get("appid");
		String elemid = inMessage.getData().get("elemid");
		String value = inMessage.getData().get("value");
		String addvalue = inMessage.getData().get("addvalue");
		String prefixes = inMessage.getData().get("prefixes");
		
		List<String> tagPrefixes = Utils.string2List(prefixes, ",");
				
		String content = Utils.tagsAsEditableHtml(appId, elemid, value, addvalue, null, tagPrefixes );

		return (ADeployer.deployContent(content,inMessage));		
	}
	
	public IMessage delete(CommonMessage inMessage){
		if (!validateData(inMessage, "appid", "elemid", "value", "delvalue", "prefixes", "dest"))
			return inMessage;
		
		String appId = inMessage.getData().get("appid");
		String elemid = inMessage.getData().get("elemid");
		String value = inMessage.getData().get("value");
		String delvalue = inMessage.getData().get("delvalue");
		String prefixes = inMessage.getData().get("prefixes");
		
		List<String> tagPrefixes = Utils.string2List(prefixes, ",");
		
		String content = Utils.tagsAsEditableHtml(appId, elemid, value, null, delvalue, tagPrefixes );

		return (ADeployer.deployContent(content,inMessage));
	}	
}
