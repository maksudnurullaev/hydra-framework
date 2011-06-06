package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.managers.CryptoManager;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class User extends AMessageHandler { // NO_UCD	

	public IMessage logout(CommonMessage inMessage) {
		Result result = new Result();
		SessionUtils.setSessionData(result, inMessage, Constants._session_user_id, null);
		if(result.isOk()){
			inMessage._user_id = null;
			inMessage.getData().put("dest", "body");
			General general = new General();
			return(general.getInitialBody(inMessage));
		}
		inMessage.setError(result.getResult());
		return inMessage;
	}
	
	public IMessage login(CommonMessage inMessage) {
		String[] mandatoryFields = {"appid","user_mail","user_password"};
		if(!testData(inMessage, mandatoryFields)) return inMessage;
		getLog().debug("All necessary fields exits");
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();
		
		String appID = inMessage.getData().get("appid");
		
		getLog().debug("Test for valid mail");
		String user_mail = inMessage.getData().get("user_mail").trim();
		String user_password = inMessage.getData().get("user_password").trim();
		String user_password_cryped = null;
		
		// 1. test for valid mail
		Utils.testFieldEMail(errorFields, errorCodes, user_mail, "user_mail");
		// 2. test for user mail existence
		if(errorCodes.size() == 0){
			getLog().debug("Test user existence");
			user_password_cryped = DBUtils.testForExistenceOfKeyAndValue(errorFields, errorCodes, appID, "User", user_mail, "password", "user_mail");
		}
		// 2.5 if error found 
		if(errorCodes.size() != 0){ 
			return highLightErrorFields(inMessage, 
					mandatoryFields,
					errorFields, errorCodes);
		}
		
		// 3. test for valid password
		Utils.test2ValidPassword(errorFields, errorCodes, user_password, "user_password");
		if(errorCodes.size() != 0){ 
			return highLightErrorFields(inMessage, 
					mandatoryFields,
					errorFields, errorCodes);
		}

		// 4. test for user/password
		if(CryptoManager.checkPassword(user_password, user_password_cryped)){ // check password
			Result result = new Result();
			SessionUtils.setSessionData(result, inMessage, Constants._session_user_id, user_mail);
			if(result.isOk()){
				inMessage._user_id = user_mail;
				inMessage.getData().put("dest", "body");
				General general = new General();
				return(general.getInitialBody(inMessage));
			}
		}
			
		inMessage.setError(MessagesManager.getText("NoData", null, inMessage._locale));
		return(inMessage);
	}

	private IMessage highLightErrorFields(CommonMessage inMessage,
			String[] mandatoryFields, List<String> errorFields,
			List<ErrorUtils.ERROR_CODES> errorCodes) {
		inMessage.clearContent();
		inMessage.setHighlightFields(errorFields);
		inMessage.setNoHighlightFields(mandatoryFields);
		inMessage.setError(Utils.getErrorDescription(errorFields, errorCodes));
		
		return(inMessage);
	}


}
