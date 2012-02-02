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
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class User extends AMessageHandler { // NO_UCD	
	private static Log _log = LogFactory.getLog("org.hydra.messages.handlers.User");

	public static IMessage logout(CommonMessage inMessage, WebContext webContext) {
		String appId = inMessage.getData().get("appid");
		_log.debug("Going to logout for: " + SessionUtils.getSessionData(webContext.getServletContext(), "_user", inMessage.getData().get("_appid")));
		SessionUtils.setSessionData(webContext.getServletContext(), "_user", appId, null);
		inMessage.setReloadPage(true);
		return(inMessage);
	}
	
	public static IMessage login(CommonMessage inMessage, WebContext context) {
		String[] mandatoryFields = {"user_mail","user_password"};
		if(!validateData(inMessage, mandatoryFields)) return inMessage;
		_log.debug("All necessary fields exits");
		String appId = inMessage.getData().get("_appid");
		
		List<String> errorFields = new ArrayList<String>();
		List<ErrorUtils.ERROR_CODES> errorCodes = new ArrayList<ErrorUtils.ERROR_CODES>();		
		
		_log.debug("Test for valid mail");
		String user_mail = inMessage.getData().get("user_mail").trim();
		String user_password = inMessage.getData().get("user_password").trim();
		String user_password_cryped = null;
		
		// 0. Test for global admin
		if(!user_mail.isEmpty() && !user_password.isEmpty()){
			_log.debug("Test administrator for user: " + user_mail);
			if(DBUtils.test4GlobalAdmin(user_mail, user_password)){
				_log.debug("Found administrator account for: " + user_mail);
				return(setupUserSession(inMessage, "+++", context));
			}
		}
		
		// 1. test for valid mail
		Utils.testFieldEMail(errorFields, errorCodes, user_mail, "user_mail");
		// 2. test for user mail existence
		if(errorCodes.size() == 0){
			_log.debug("Test user existence");
			user_password_cryped = DBUtils.testForExistenceOfKeyAndValue(errorFields, errorCodes, appId, "User", user_mail, "password", "user_mail");
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
			
		inMessage.setError(MessagesManager.getText("NoData", null, inMessage.getData().get("_locale")));
		return(inMessage);
	}

	private static IMessage setupUserSession(CommonMessage inMessage, String userId, WebContext webContext) {
		String appId = inMessage.getData().get("_appid");
		SessionUtils.setSessionData(webContext.getServletContext(), "_user", appId, userId);
		inMessage.setReloadPage(true);
		return(inMessage);
	}

	private static IMessage highLightErrorFields(CommonMessage inMessage,
			String[] mandatoryFields, List<String> errorFields,
			List<ErrorUtils.ERROR_CODES> errorCodes) {
		inMessage.clearContent();
		inMessage.setHighlightFields(errorFields);
		inMessage.setNoHighlightFields(mandatoryFields);
		inMessage.setError(Utils.getErrorDescription(errorFields, errorCodes));
		
		return(inMessage);
	}


}
