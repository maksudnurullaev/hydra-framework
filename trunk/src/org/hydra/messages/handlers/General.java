package org.hydra.messages.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.deployers.ADeployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.SessionUtils;

public class General extends AMessageHandler { // NO_UCD
	private static Log _log = LogFactory.getLog("org.hydra.messages.handlers.General");
	public static IMessage getTextByKey(CommonMessage inMessage) {
		if (!validateData(inMessage, "key"))
			return inMessage;
		
		String content = MessagesManager.getText(
				inMessage.getData().get("key"),
				"div",
				inMessage.getData().get("locale"));

		return (ADeployer.deployContent(content,inMessage));
	}

	public static void changeLocale(IMessage inMessage, WebContext webContext) {
		if (!validateData(inMessage, "locale"))return;
		String locale = inMessage.getData().get("locale");
		String appId = inMessage.getData().get("appid");
		
		SessionUtils.setSessionData(webContext.getSession(), "locale", appId, locale);
		
		_log.debug("set locale to: " + 
				SessionUtils.getSessionData(webContext, 
				"locale", 
				appId));		
	}
	
	public static IMessage getInitialBody(CommonMessage inMessage) {
		boolean is_mobile = 
				inMessage.getData() != null 
				&& inMessage.getData().get("browser") != null 
				&& inMessage.getData().get("browser").equalsIgnoreCase("mobile");
		String appId = inMessage.getData().get("appid");

		String content = "";
		if(is_mobile && FileUtils.isExistAppHtmlFile(appId, "body.mobile")){
			content = FileUtils.getHtmlFromFile(appId,"body.mobile");
		}else{
			content = FileUtils.getHtmlFromFile(appId,"body");			
		}
		if(content != null){
			_log.debug(String.format("deploy connent for (appid/locale): ", 
					inMessage.getData().get("appid"), 
					inMessage.getData().get("locale")));
			return(ADeployer.deployContent(content,inMessage));
		}
		inMessage.setHtmlContent("Could not find initial body for: " + inMessage.getData().get("appid"));
		return(inMessage);
	};

	public static IMessage getContent(CommonMessage inMessage){
		if (!validateData(inMessage, "content"))
			return inMessage;
		
		String content = inMessage.getData().get("content");
		_log.debug("Try to get content for: " + content);
		
		if(!content.isEmpty())
			ADeployer.deployContent(content,inMessage);
		else
			inMessage.setError("_error_empty_request_");

		return inMessage;
	};	
	
	public static IMessage getHAKDContent(CommonMessage inMessage){
		if (!validateData(inMessage, "hakdContent"))
			return inMessage;
		
		String hakdContent = inMessage.getData().get("hakdContent");
		inMessage.getData().put("content", "[[" + hakdContent + "]]");
		
		return(getContent(inMessage));
		
	};
}
