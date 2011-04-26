package org.hydra.messages.handlers;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;

public class General extends AMessageHandler { // NO_UCD
	public static final String _body_html_path_format = "/h/index_%s_%s.html";

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
		if (!testParameters(inMessage, "key"))
			return inMessage;
		getLog().debug(
				"Try to change current locale to: "
						+ inMessage.getData().get("key"));

		// change session
		String new_locale = inMessage.getData().get("key");

		Result result = new Result();
		SessionUtils.setSessionData(result, inMessage, Constants._data_locale,
				new_locale);
		// if something wrong
		if (!result.isOk()) {
			inMessage.setError(result.getResult());
			return inMessage;
		}
		getLog().debug("Locale sucessefully changed to: " + new_locale);
		// Change message locale too...
		inMessage._locale = new_locale;
		return getInitialHTMLElements(inMessage);
	}

	public IMessage getInitialHTMLElements(CommonMessage inMessage) {
		getLog().debug("get stylesheets");
		inMessage.setStyleSheets(inMessage._web_application.getStylesheets());
		String path2File = String.format(_body_html_path_format,
				inMessage._web_application.getId(),
				inMessage._locale);
		path2File = inMessage._web_context.getServletContext().getRealPath(
				path2File);
		getLog().debug("get html content from file: " + path2File);
		Result result = forwardToString(path2File);
		if (result.isOk()) {
			getLog().debug("Deploy content length: " + result.getResult().length());
			getLog().debug("... App ID: " + inMessage._web_application.getId());
			getLog().debug("... Locale: " + inMessage._locale);
			getLog().debug("... User ID: " + inMessage._user_id);
			inMessage.setHtmlContent(
					Utils.deployContent(
							result.getResult(),
							inMessage._web_application.getId(),
							inMessage._locale,
							inMessage._user_id)
					);

		} else
			inMessage.setError(result.getResult());
		return inMessage;
	}

	public Result forwardToString(String inPath2File) {
		Result result = new Result();
		try {
			File file = new File(inPath2File);
			result.setResult(FileUtils.readFileToString(file,
					DBUtils._utf8_encoding));
			result.setResult(true);
		} catch (Exception e) {
			_log.error(e.toString());
			result.setResult("Internal server error!");
			result.setResult(false);
		}
		return result;
	}
}
