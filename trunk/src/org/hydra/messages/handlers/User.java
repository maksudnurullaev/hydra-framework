package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.WebContext;
import org.hydra.managers.CryptoManager;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class User extends AMessageHandler { // NO_UCD	

	public IMessage logout(CommonMessage inMessage, WebContext context) {
		Result result = new Result();
		SessionUtils.setSessionData(result, inMessage, "user", null, context);
		if(result.isOk()){
			inMessage.setReloadPage(true);
			return(inMessage);
		}
		inMessage.setError(result.getResult());
		return inMessage;
	}
	
	public IMessage login(CommonMessage inMessage, WebContext context) {
		String[] mandatoryFields = {"_appid","user_mail","user_password"};
		if(!validateData(inMessage, mandatoryFields)) return inMessage;
		getLog().debug("All necessary fields exits");
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();
		
		String appID = inMessage.getData().get("_appid");
		
		getLog().debug("Test for valid mail");
		String user_mail = inMessage.getData().get("user_mail").trim();
		String user_password = inMessage.getData().get("user_password").trim();
		String user_password_cryped = null;
		
		// 0. Test for global admin
		if(!user_mail.isEmpty() && !user_password.isEmpty()){
			getLog().debug("Test administrator for user: " + user_mail);
			if(DBUtils.test4GlobalAdmin(user_mail, user_password)){
				getLog().debug("Found administrator account for: " + user_mail);
				return(setupUserSession(inMessage, "+++", context));
			}
		}
		
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

		// 3. test for user/password
		if(CryptoManager.checkPassword(user_password, user_password_cryped)){ // check password
			return(setupUserSession(inMessage, user_mail, context));
		}
			
		inMessage.setError(MessagesManager.getText("NoData", null, inMessage.getLocale()));
		return(inMessage);
	}

	private IMessage setupUserSession(CommonMessage inMessage, String userId, WebContext context) {
		Result result = new Result();
		SessionUtils.setSessionData(result, inMessage, "user", userId, context);
		if(result.isOk()){
			inMessage.setReloadPage(true);
			return(inMessage);
		}
		inMessage.clearContent();
		inMessage.setError(result.toString());
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
