package org.hydra.messages.handlers;


import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Moder;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class General extends AMessageHandler { // NO_UCD
	public IMessage getTextByKey(CommonMessage inMessage) {
		if (!testParameters(inMessage, "key"))
			return inMessage;
		inMessage.setHtmlContent(
				MessagesManager.getText(
						inMessage.getData().get("key"),
						"div",
						inMessage._locale));

		return inMessage;
	}

	public IMessage changeLocale(CommonMessage inMessage) {
		if (!testParameters(inMessage, Constants._session_locale, Constants._session_url))
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
		
		return getInitalBody(inMessage);
	}

	public IMessage getInitialHTMLElements(CommonMessage inMessage) {
		if (!testParameters(inMessage, Constants._session_url))
			return inMessage;
		
		Result result = new Result();
		
		// **** save session's moder
		SessionUtils.setSessionURLWrapper(result, inMessage);
		
		// **** set new URLWrapper
		SessionUtils.getSessionURLWrapper(result, inMessage);

		// **** stylesheets
		getLog().debug("get stylesheets");
		inMessage.setStyleSheets(inMessage._web_application.getStylesheets());
		
		// **** html's <body>
		getInitalBody(inMessage);
		
		return inMessage;
	}

	private IMessage getInitalBody(CommonMessage inMessage) {
		Result result = new Result();
		String path2File = String.format("/h/body_%s.html",inMessage._web_application.getId());
		path2File = inMessage._web_context.getServletContext().getRealPath(path2File);
		getLog().debug("get html's body from: " + path2File);
		
		Utils.getFileAsString(result,path2File);
		if (result.isOk()) {
			String content = (String) result.getObject();
			if(!result.isOk()){
				inMessage.setError(result.getResult());
				result.setResult(false);
			}else{
				getLog().debug("... HTML body content length: " + content.length());
				getLog().debug("... App ID: " + inMessage._web_application.getId());
				getLog().debug("... Locale: " + inMessage._locale);
				getLog().debug("... User ID: " + inMessage._user_id);
				inMessage.setHtmlContent(Utils.deployContent(content,inMessage));
				result.setResult(true);
			}

		} else {
			inMessage.setError(result.getResult());
			result.setResult(false);
		}
		return inMessage;
	}
}
