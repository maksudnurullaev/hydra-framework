package org.hydra.messages.handlers;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.hydra.deployers.ADeployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;

public class General extends AMessageHandler { // NO_UCD
	public IMessage getTextByKey(CommonMessage inMessage) {
		if (!testData(inMessage, "key"))
			return inMessage;
		
		String content = MessagesManager.getText(
				inMessage.getData().get("key"),
				"div",
				inMessage._locale);

		return (ADeployer.deployContent(content,inMessage));
	}

	public IMessage changeLocale(CommonMessage inMessage) {
		if (!testData(inMessage, Constants._session_locale))
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
		
		if(!content.isEmpty())
			ADeployer.deployContent(content,inMessage);
		else
			inMessage.setError("_error_empty_request_");

		return inMessage;
	};
	
	public IMessage loadCSSFile(CommonMessage inMessage){
		getLog().debug("get stylesheets for: " + inMessage._web_application.getId());
		inMessage.setStyleSheet(inMessage._web_application.getStyleSheet());
		inMessage.clearContent();
		return inMessage;
	};
	
	public IMessage loadJSFile(CommonMessage inMessage){
		inMessage.setJSFile(String.format("jscripts/%s.js", inMessage._web_application.getId()));
		inMessage.clearContent();
		return inMessage;
	};	

	public IMessage getInitialBody(CommonMessage inMessage) {		
		getLog().debug("... App ID: " + inMessage._web_application.getId());
		getLog().debug("... Locale: " + inMessage._locale);
		getLog().debug("... User ID: " + inMessage._user_id);
		
		String content = "NOT_DEFINED";
		if(inMessage._web_application.isManager())
			content = MessagesManager.getTemplate("template.html.body");
		else{
			content = getBodyFromFile(inMessage._web_context.getServletContext(), inMessage._web_application.getId());
		}
		
		getLog().debug("... HTML body content length: " + content.length());
		
		
		return(ADeployer.deployContent(content,inMessage));
	}

	private String getBodyFromFile(ServletContext servletContext, String inAppId) {
				
		String content = "CONTENT_NOT_FOUND";
		String filePath = String.format("/body/%s.body", inAppId);
		File file = new File(servletContext.getRealPath(filePath));
		try {
			content = FileUtils.readFileToString(file,"UTF8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return(content); 
	};
}
