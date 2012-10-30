package org.hydra.messages.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.deployers.ADeployer;
import org.hydra.deployers.Dictionary;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.FileUtils;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class General extends AMessageHandler { // NO_UCD
	private static Log _log = LogFactory.getLog("org.hydra.messages.handlers.General");
	public static IMessage getTextByKey(CommonMessage inMessage) {
		if (!validateData(inMessage, Constants._key))
			return inMessage;
		
		String 	wrapHtmlElement = Utils.getMessageDataOrNull(inMessage, "wrap");
		
		String content = Dictionary.getTextByKey(
				Utils.getMessageDataOrNull(inMessage, Constants._key),
				wrapHtmlElement,
				inMessage);

		return (ADeployer.deployContent(content,inMessage));
	}

	// just for some tests!
	public static void dumpMethod(CommonMessage inMessage){
		inMessage.setError("Dump method processed!");
		_log.warn("Dump method processed!");
	}
	
	public static void changeLocale(IMessage inMessage, WebContext webContext) {
		if (!validateData(inMessage, Constants._locale_key))return;
		String locale = Utils.getMessageDataOrNull(inMessage, Constants._locale_key);
		String appId = Utils.getMessageDataOrNull(inMessage, Constants._appid_key);
		
		SessionUtils.setSessionData(webContext.getSession(), Constants._locale_key, appId, locale);
		
		_log.debug("set locale to: " + 
				SessionUtils.getSessionData(webContext, 
				Constants._locale_key, 
				appId));		
	}
	
	public static IMessage getInitialBody(CommonMessage inMessage) {
		String browser  = Utils.getMessageDataOrNull(inMessage, "browser");
		boolean is_mobile = (browser != null && browser.equalsIgnoreCase("mobile"));
		String appId = Utils.getMessageDataOrNull(inMessage, Constants._appid_key);

		String content = "";
		if(is_mobile && FileUtils.isExistAppHtmlFile(appId, "body.mobile")){
			content = FileUtils.getHtmlFromFile(appId,"body.mobile");
		}else{
			content = FileUtils.getHtmlFromFile(appId,"body");			
		}
		if(content != null){
			_log.debug(String.format("deploy connent for (appid/locale): %s/%s", 
					Utils.getMessageDataOrNull(inMessage, Constants._appid_key), 
					Utils.getMessageDataOrNull(inMessage, Constants._locale_key)));
			return(ADeployer.deployContent(content,inMessage));
		}
		inMessage.setHtmlContent("Could not find initial body for: " + Utils.getMessageDataOrNull(inMessage, Constants._appid_key));
		return(inMessage);
	};

	public static IMessage getContent(CommonMessage inMessage){
		if (!validateData(inMessage, Constants._content_key))
			return inMessage;
		
		String content = Utils.getMessageDataOrNull(inMessage, Constants._content_key);
		_log.debug("Try to get content for: " + content);
		
		if(content != null && !content.isEmpty())
			ADeployer.deployContent(content,inMessage);
		else
			inMessage.setError("_error_empty_request_");

		return inMessage;
	};	
}
