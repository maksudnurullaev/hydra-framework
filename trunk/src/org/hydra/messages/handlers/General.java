package org.hydra.messages.handlers;

import org.hydra.deployers.ADeployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;

public class General extends AMessageHandler { // NO_UCD
	public IMessage getTextByKey(CommonMessage inMessage) {
		if (!validateData(inMessage, "key"))
			return inMessage;
		
		String content = MessagesManager.getText(
				inMessage.getData().get("key"),
				"div",
				inMessage.getLocale());

		return (ADeployer.deployContent(content,inMessage));
	}

	public IMessage changeLocale(CommonMessage inMessage) {
		if (!validateData(inMessage, Constants._session_locale))
			return inMessage;
		getLog().debug(
				"Try to change current locale to: "
						+ inMessage.getData().get(Constants._session_locale));

		// change session
		String new_locale = inMessage.getData().get(Constants._session_locale);

		Result result = new Result();
		SessionUtils.setSessionData(
				result, 
				inMessage, 
				Constants._session_locale,
				new_locale);
		// if something wrong
		if (!result.isOk()) {
			inMessage.setError(result.getResult());
			return inMessage;
		}
		getLog().debug("Locale sucessefully changed to: " + new_locale);
		// Change message locale too...
		inMessage.setLocale(new_locale);
		
		return getInitialBody(inMessage);
	}
	
	public IMessage getContent(CommonMessage inMessage){
		if (!validateData(inMessage, "content"))
			return inMessage;
		
		String content = inMessage.getData().get("content");
		getLog().debug("Try to get content for: " + content);
		
		if(!content.isEmpty())
			ADeployer.deployContent(content,inMessage);
		else
			inMessage.setError("_error_empty_request_");

		return inMessage;
	};
	
	public IMessage getInitialBody(CommonMessage inMessage) {		
		String content = FileUtils.getFromHtmlFile(inMessage.getWebApplication().getId(),"body");
		if(content != null){
			getLog().debug("... HTML body content length: " + content.length());
			return(ADeployer.deployContent(content,inMessage));
		}
		inMessage.setHtmlContent("Could not find initial body for: " + inMessage.getWebApplication().getId());
		return(inMessage);
	};
}
