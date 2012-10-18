package org.hydra.messages.handlers;


import java.util.ArrayList;
import java.util.List;

import org.hydra.deployers.ADeployer;
import org.hydra.html.fields.FieldInput;
import org.hydra.html.fields.IField;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Utils;

public class AdmTags extends AMessageHandler {

	public IMessage list(CommonMessage inMessage){
		if(!validateData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Tags|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
		
		return(ADeployer.deployContent(content,inMessage));
	}
	
	public IMessage addForm(CommonMessage inMessage){
		if(!validateData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		
		ArrayList<IField> fields = new ArrayList<IField>();
		fields.add(new FieldInput("tag_new", "NewName"));		
		
		String form = Utils.generateForm(
				String.format("<h4>[[Dictionary|Text|New_Tag|span]]</h4>"), appId, 
				"AdmTags", "add", 
				"AdmTags", "list", 
				Constants._admin_app_action_div, fields, null, inMessage);
		
		return(ADeployer.deployContent(form,inMessage));			
	}

	public IMessage add(CommonMessage inMessage){
		if(!validateData(inMessage, "appid", "tag_new")) return inMessage;
		String appID = inMessage.getData().get("appid");
		String key = inMessage.getData().get("tag_new").trim();
		String cfName = "Tag";
		
		if(key.isEmpty()){
			inMessage.setError("NO data");
			return inMessage;
		}
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();		
		
		DBUtils.testForNonExistenceOfKey(errorFields, errorCodes, appID, cfName, key, "tag_new");		
		
		if(errorFields.size() != 0){
			inMessage.clearContent();
			inMessage.setHighlightFields(errorFields);
			inMessage.setError(Utils.getErrorDescription(errorFields, errorCodes));
			return inMessage;
		}		
		
		inMessage.getData().put("dest", Constants._admin_app_action_div);
		DBUtils.setValue(appID, cfName, key, "name", key);
				
		return(list(inMessage));
	}
	
	public IMessage delete(CommonMessage inMessage){
		if(!validateData(inMessage, "appid", "value")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String value = inMessage.getData().get("value").trim();
				
		String inColumnFamily = "Tag";
		
		inMessage.getData().put("dest", Constants._admin_app_action_div);
		ErrorUtils.ERROR_CODES errCode = DBUtils.deleteKeys(appId, inColumnFamily, value);
		if(errCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(list(inMessage));
	}	
}
