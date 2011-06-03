package org.hydra.messages.handlers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hydra.deployers.ADeployer;
import org.hydra.html.fields.FieldInput;
import org.hydra.html.fields.FieldSelectTag;
import org.hydra.html.fields.FieldTextArea;
import org.hydra.html.fields.IField;
import org.hydra.managers.CryptoManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Utils;
import org.hydra.utils.DBUtils.ERROR_CODES;

public class AdmUsers extends AMessageHandler {

	public IMessage list(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Users|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
				
		return(ADeployer.deployContent(content,inMessage));
	}	
	
	public IMessage addForm(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		ArrayList<IField> fields = new ArrayList<IField>();
		fields.add(new FieldInput("user_mail", ""));
		fields.add(new FieldInput("user_password", "", "password"));
		fields.add(new FieldInput("user_password2", "", "password"));
		
		List<String> tagPrefixes = new ArrayList<String>();
		tagPrefixes.add("User");
		fields.add(new FieldSelectTag(appId, "user_tag", "", tagPrefixes));
		
		ArrayList<IField> optionaFields = new ArrayList<IField>();
		optionaFields.add(new FieldTextArea("user_info", "", "style=\"width: 25em; height: 5em; border: 1px solid #7F9DB9;\""));
		
		String form = Utils.generateForm(
				String.format("<h4>[[DB|Text|New_User|locale]]</h4>"), appId, 
				"AdmUsers", "add", 
				"AdmUsers", "list", 
				"admin.app.action", fields, optionaFields);
		
		return(ADeployer.deployContent(form,inMessage));		
	}	
	
	public IMessage add(CommonMessage inMessage){
		String[] mandatoryFields = {"appid","user_mail","user_password","user_password2","user_tag"};
		if(!testData(inMessage, mandatoryFields)) return inMessage;
		
		
		List<String> errorFields = new ArrayList<String>();
		String appID = inMessage.getData().get("appid");
		
		Utils.testFieldEMail(errorFields,inMessage, "user_mail");
		Utils.test2Passwords(errorFields, inMessage, "user_password", "user_password2");
		
		if(errorFields.size() != 0){
			inMessage.clearContent();
			inMessage.setHighlightFields(errorFields);
			inMessage.setNoHighlightFields(mandatoryFields);
			return inMessage;
		}
		
		String key = inMessage.getData().get("user_mail").trim();
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("password", CryptoManager.encryptPassword(inMessage.getData().get("user_password").trim()));
		fields.put("tag", inMessage.getData().get("user_tag"));
		if(inMessage.getData().get("user_info") != null &&
				!inMessage.getData().get("user_info").trim().isEmpty()){
				fields.put("info", inMessage.getData().get("user_info").trim());
		}
		
		for(Map.Entry<String, String> entry: fields.entrySet()){
			ERROR_CODES errorCode = DBUtils.setValue(appID, "User", key, entry.getKey(), entry.getValue());
			if(errorCode != ERROR_CODES.NO_ERROR){
				inMessage.setError("Error: " + errorCode);
				return inMessage;
			}
			System.out.println(String.format("%s: %s", entry.getKey(), entry.getValue()));
		}
		
		inMessage.setError("OK");
		// finish
		return inMessage;
	}
}
