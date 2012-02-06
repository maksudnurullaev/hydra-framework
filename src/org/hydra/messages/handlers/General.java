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
		if (!validateData(inMessage, "_key"))
			return inMessage;
		
		String content = MessagesManager.getText(
				inMessage.getData().get("_key"),
				"div",
				inMessage.getData().get("_locale"));

		return (ADeployer.deployContent(content,inMessage));
	}

	public static IMessage changeLocale(IMessage inMessage, WebContext webContext) {
		if (!validateData(inMessage, "_locale"))
			return inMessage;

		String locale = inMessage.getData().get("_locale");
		String appId = inMessage.getData().get("_appid");
		
		SessionUtils.setSessionData(webContext.getServletContext(), "_locale", appId, locale);
		
		_log.debug("set locale to: " + 
				SessionUtils.getSessionData(webContext.getServletContext(), 
				"_locale", 
				appId));
				
		return getInitialBody(inMessage, webContext);
	}
	
	public static IMessage getInitialBody(IMessage inMessage, WebContext webContext) {		
		String content = FileUtils.getFromHtmlFile(inMessage.getData().get("_appid"),"body", webContext.getServletContext());
		if(content != null){
			_log.debug(String.format("deploy connent for (appid/locale): ", 
					inMessage.getData().get("_appid"), 
					inMessage.getData().get("_locale")));
			return(ADeployer.deployContent(content,inMessage));
		}
		inMessage.setHtmlContent("Could not find initial body for: " + inMessage.getData().get("_appid"));
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
}
