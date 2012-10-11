package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.managers.CryptoManager;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.ErrorUtils.ERROR_CODES;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class User extends AMessageHandler { // NO_UCD	
	private static Log _log = LogFactory.getLog("org.hydra.messages.handlers.User");

	public static IMessage logout(IMessage inMessage, WebContext webContext) {
		String appId = inMessage.getData().get("appid");
		_log.debug("Going to logout for: " + SessionUtils.getSessionData(webContext, "_userid", inMessage.getData().get("appid")));
		SessionUtils.setSessionData(webContext.getSession(), "_userid", appId, null);
		SessionUtils.setSessionData(webContext.getSession(), "_roles", appId, null);
		inMessage.setReloadPage(true);
		return(inMessage);
	}
	
	public static IMessage changePassword(CommonMessage inMessage) {
		String[] mandatoryFields = {"user_password", "user_password1", "user_password2"};
		if(!validateData(inMessage, mandatoryFields)) return inMessage;
		
		String appID = inMessage.getData().get("appid");
		String user_mail = inMessage.getData().get("_userid");
		
		if(user_mail == null || user_mail.isEmpty()){
			inMessage.setError("Not logged in!");
			return(inMessage);
		}
		
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();		

		String user_password = inMessage.getData().get("user_password").trim();
		
		Utils.test2ValidPassword(errorFields, errorCodes, user_password, "user_password");
		Utils.test2ValidPasswords(errorFields, errorCodes, inMessage, "user_password1", "user_password2");
		if(!errorFields.isEmpty()) return(highLightErrorFields(inMessage, mandatoryFields, errorFields, errorCodes));		
		String user_password_cryped = DBUtils.testForExistenceOfKeyAndValue(errorFields, errorCodes, appID, "User", user_mail, "password", "password");
		if(!errorFields.isEmpty()) return(highLightErrorFields(inMessage, mandatoryFields, errorFields, errorCodes));		

		if(!CryptoManager.checkPassword(user_password, user_password_cryped)){
			inMessage.setError(MessagesManager.getText("NoCorrectLoginData", null, inMessage.getData().get("locale")));
			return(inMessage);			
		}		
		
		if(inMessage.getData().get("user_password1").trim().equals(user_password)){
			inMessage.setError("Error: Nothing to change!");
			return(inMessage);			
		}
		
		user_password = inMessage.getData().get("user_password1").trim();
		user_password = CryptoManager.encryptPassword(user_password);
		
		ErrorUtils.ERROR_CODES errorCode = DBUtils.setValue(appID, "User", user_mail, "password", user_password);
		if(errorCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError("Error: Db!");
			return(inMessage);
		}
		
		inMessage.setHtmlContents("password_panel","OK:Password changed!");
		return(inMessage);
	}
	public static IMessage login(IMessage inMessage, WebContext context) {
		String[] mandatoryFields = {"user_mail","user_password"};
		if(!validateData(inMessage, mandatoryFields)) return inMessage;
		_log.debug("All necessary fields exits");
		String appId = inMessage.getData().get("appid");
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();		
		
		_log.debug("Test for valid mail");
		String user_mail = inMessage.getData().get("user_mail").trim();
		String user_password = inMessage.getData().get("user_password").trim();
		String user_password_cryped = null;
		String user_roles = "";
		
		// 0. Test for global admin
		if(!user_mail.isEmpty() && !user_password.isEmpty()){
			_log.debug("Test administrator for user: " + user_mail);
			if(DBUtils.test4GlobalAdmin(user_mail, user_password)){
				_log.debug("Found administrator account for: " + user_mail);
				return(setupUserSession(inMessage, "*", "*", context));
			}
		}else{
			if(user_mail.isEmpty()){
				errorFields.add("user_mail");
				errorCodes.add(ERROR_CODES.ERROR_NO_VALID_EMAIL);
			}
			if(user_password.isEmpty()){
				errorFields.add("user_password");
				errorCodes.add(ERROR_CODES.ERROR_NO_VALID_PASSWORD);
			}
			if(errorCodes.size() != 0){ 
				return highLightErrorFields(inMessage, 
						mandatoryFields,
						errorFields, errorCodes);
			}			
		}
		
		// 1. test for valid mail
		Utils.testFieldEMail(errorFields, errorCodes, user_mail, "user_mail");
		// 2. test for user mail existence
		if(errorCodes.size() == 0){
			_log.debug("User exist!");
			user_password_cryped = DBUtils.testForExistenceOfKeyAndValue(errorFields, errorCodes, appId, "User", user_mail, "password", "password");
		}
		// 2.5 if error found 
		if(errorCodes.size() != 0){ 
			return highLightErrorFields(inMessage, 
					mandatoryFields,
					errorFields, errorCodes);
		}
		// 2.6 get user roles
		user_roles = DBUtils.testForExistenceOfKeyAndValue(errorFields, errorCodes, appId, "User", user_mail, "tag", "tag");

		// 3. test for user/password
		if(CryptoManager.checkPassword(user_password, user_password_cryped)){ // check password
			return(setupUserSession(inMessage, user_mail, user_roles, context));
		}
			
		inMessage.setError(MessagesManager.getText("NoLogin", null, inMessage.getData().get("locale")));
		return(inMessage);
	}

	private static IMessage setupUserSession(IMessage inMessage, String userId, String roles, WebContext webContext) {
		
		String appId = inMessage.getData().get("appid");
		SessionUtils.setSessionData(webContext.getSession(), "_userid", appId, userId);
		SessionUtils.setSessionData(webContext.getSession(), "_roles", appId, roles);
		inMessage.setReloadPage(true);
		return(inMessage);
	}

	private static IMessage highLightErrorFields(IMessage inMessage,
			String[] mandatoryFields, List<String> errorFields,
			List<ErrorUtils.ERROR_CODES> errorCodes) {
		inMessage.clearContent();
		inMessage.setHighlightFields(errorFields);
		inMessage.setNoHighlightFields(mandatoryFields);
		inMessage.setError(Utils.getErrorDescription(errorFields, errorCodes));
		
		return(inMessage);
	}


}
