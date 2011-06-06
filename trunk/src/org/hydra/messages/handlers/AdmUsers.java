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
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Utils;

public class AdmUsers extends AMessageHandler {

	public IMessage list(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Users|%s|html]]", appId);
		getLog().error("Try to get content for: " + content);
				
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
		getLog().debug("All necessary fields exits");
		
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();
		
		String appID = inMessage.getData().get("appid");
		
		getLog().debug("Test for valid mail");
		String user_mail = inMessage.getData().get("user_mail").trim();
		Utils.testFieldEMail(errorFields, errorCodes, user_mail, "user_mail");
		
		if(errorCodes.size() == 0){
			getLog().debug("Test for user existence");
			DBUtils.testForNonExistenceOfKeyOrValue(errorFields, errorCodes, appID, "User", user_mail, "password", "user_mail");
		}

		getLog().debug("Test for valid password");
		Utils.test2ValidPasswords(errorFields, errorCodes, inMessage, "user_password", "user_password2");
		
		if(errorFields.size() != 0){
			getLog().error("Errors found");
			inMessage.clearContent();
			inMessage.setHighlightFields(errorFields);
			inMessage.setNoHighlightFields(mandatoryFields);
			inMessage.setError(Utils.getErrorDescription(errorFields, errorCodes));
			return inMessage;
		}
		
		getLog().debug("Create key/value data map");
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("password", CryptoManager.encryptPassword(inMessage.getData().get("user_password").trim()));
		fields.put("tag", inMessage.getData().get("user_tag"));
		if(inMessage.getData().get("user_info") != null &&
				!inMessage.getData().get("user_info").trim().isEmpty()){
				fields.put("info", inMessage.getData().get("user_info").trim());
		}
		
		getLog().debug("All ok, try to add new data...");
		for(Map.Entry<String, String> entry: fields.entrySet()){
			ErrorUtils.ERROR_CODES errorCode = DBUtils.setValue(appID, "User", user_mail, entry.getKey(), entry.getValue());
			if(errorCode != ErrorUtils.ERROR_CODES.NO_ERROR){
				inMessage.setError("Error: " + errorCode);
				return inMessage;
			}
			System.out.println(String.format("%s: %s", entry.getKey(), entry.getValue()));
		}
		// finish
		return list(inMessage);
	}

	public IMessage delete(CommonMessage inMessage){
		if(!testData(inMessage, "appid", "value")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String value = inMessage.getData().get("value").trim();
				
		String inColumnFamily = "User";
		ErrorUtils.ERROR_CODES errCode = DBUtils.deleteKey(appId, inColumnFamily, value);
		if(errCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(list(inMessage));		
	}
}
