package org.hydra.messages.handlers;


import java.util.ArrayList;

import org.hydra.deployers.ADeployer;
import org.hydra.html.fields.FieldInput;
import org.hydra.html.fields.IField;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Utils;

public class AdmTags extends AMessageHandler {

	public IMessage list(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Tags|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
		
		return(ADeployer.deployContent(content,inMessage));
	}
	
	public IMessage addForm(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		
		ArrayList<IField> fields = new ArrayList<IField>();
		fields.add(new FieldInput("tag_new", "NewName"));		
		
		String form = Utils.generateForm(
				String.format("<h4>[[DB|Text|New_Tag|locale]]</h4>"), appId, 
				"AdmTags", "add", 
				"AdmTags", "list", 
				"admin.app.action", fields, null);
		
		return(ADeployer.deployContent(form,inMessage));			
	}

	public IMessage add(CommonMessage inMessage){
		if(!testData(inMessage, "appid", "tag_new")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String value = inMessage.getData().get("tag_new").trim();
		
		if(value.isEmpty()){
			inMessage.setError("NO data");
			return inMessage;
		}
		
		String inColumnFamily = "Tag";
		
		inMessage.getData().put("dest", "admin.app.action");
		DBUtils.setValue(appId, inColumnFamily, value, "name", value);
				
		return(list(inMessage));
	}
	
	public IMessage delete(CommonMessage inMessage){
		if(!testData(inMessage, "appid", "value")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String value = inMessage.getData().get("value").trim();
				
		String inColumnFamily = "Tag";
		
		inMessage.getData().put("dest", "admin.app.action");
		ErrorUtils.ERROR_CODES errCode = DBUtils.deleteKey(appId, inColumnFamily, value);
		if(errCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(list(inMessage));
	}	
}