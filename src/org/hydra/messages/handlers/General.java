package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.deployers.Deployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class General extends AMessageHandler { // NO_UCD
	public IMessage getTextByKey(CommonMessage inMessage) {
		if (!testData(inMessage, "key"))
			return inMessage;
		inMessage.setHtmlContent(
				MessagesManager.getText(
						inMessage.getData().get("key"),
						"div",
						inMessage._locale));

		return inMessage;
	}

	public IMessage changeLocale(CommonMessage inMessage) {
		if (!testData(inMessage, Constants._session_locale, Constants._session_url))
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
		inMessage._locale = new_locale;
		
		return getInitialBody(inMessage);
	}
	
	public IMessage getContent(CommonMessage inMessage){
		if (!testData(inMessage, "key"))
			return inMessage;
		
		String content = inMessage.getData().get("key");
		getLog().debug("Try to get content for: " + content);
		
		if(!content.isEmpty()){
			List<String> links = new ArrayList<String>();
			String htmlContent = Deployer.deployContent(content,inMessage, links);
			inMessage.setHtmlContent(htmlContent);
			inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
		} else {
			inMessage.setError("_error_empty_request_");
		}
		return inMessage;
	};
	
	public IMessage deployInitialFiles(CommonMessage inMessage){
		if (!testData(inMessage, Constants._session_url))
			return inMessage;

		if (!testData(inMessage, Constants._session_url))
			return inMessage;
		
		Result result = new Result();
		
		// **** save session's URL
		SessionUtils.setSessionURLWrapper(result, inMessage);
		
		// **** get session's URL
		SessionUtils.getSessionURLWrapper(result, inMessage);

		// **** stylesheets
		getLog().debug("get stylesheets");
		inMessage.setStyleSheets(inMessage._web_application.getStylesheets());
		
		// **** html's personal initial jscript
		inMessage.setJscript(
				String.format("jscripts/%s.js", inMessage._web_application.getId())
				, "Globals.setHtmlBody");
		
		return inMessage;
	}

	public IMessage getInitialBody(CommonMessage inMessage) {
		
		getLog().debug("... App ID: " + inMessage._web_application.getId());
		getLog().debug("... Locale: " + inMessage._locale);
		getLog().debug("... User ID: " + inMessage._user_id);
		
		String content = MessagesManager.getTemplate("template.html.body");
		getLog().debug("... HTML body content length: " + content.length());
		
		List<String> links = new ArrayList<String>();
		String htmlContent = Deployer.deployContent(content,inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));	
		
		return inMessage;
	}
}
