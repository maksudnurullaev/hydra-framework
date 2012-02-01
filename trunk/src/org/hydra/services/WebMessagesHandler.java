package org.hydra.services;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.MessagesCollector;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.MessageBean;
import org.hydra.messages.handlers.General;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;
import org.hydra.utils.abstracts.ALogger;

public class WebMessagesHandler extends ALogger {
	public Object[] sendMessage(MessageBean inMessage) throws RichedMaxCapacityException {
		return sendMessage(inMessage, null);
	}
	
	public Object[] sendMessage(MessageBean inMessage, FileTransfer inFile) throws RichedMaxCapacityException {
		List<MessageBean> resultList = new ArrayList<MessageBean>();
		// Detect web context
		if(WebContextFactory.get() == null){
			getLog().error("WebContext is null!");
			inMessage.setError("WebContext is null!");
			resultList.add(inMessage);
			return(resultList.toArray());
		}
		WebContext webContext = WebContextFactory.get();
		// Attach session's data
		Result result = new Result();
		SessionUtils.setApplicationData(result, inMessage, webContext);
		if (!result.isOk()) {
			inMessage.setError(result.getResult());
			resultList.add(inMessage);
			return resultList.toArray();
		}
		// test for action with session
		if(handledWithSession(inMessage, webContext)){
			resultList.add(inMessage);
			return resultList.toArray();			
		}		
		// test captcha if needs
		if(inMessage.getData().containsKey(Constants._captcha_value)
				&& !SessionUtils.validateCaptcha(inMessage, webContext)){
			ArrayList<String> errorFields = new ArrayList<String>();
			errorFields.add(Constants._captcha_value);
			inMessage.setHighlightFields(errorFields);
			resultList.add(inMessage);
			return resultList.toArray();
		}		
		
		// sets for file
		if(inFile != null){
			inMessage.setRealFilePath(webContext.getServletContext().getRealPath(inMessage.getData().get("_file_path")));
		}
		
		// set message collector
		MessagesCollector messagesCollector = null;
		BeansUtils.getWebContextBean(result, Constants._bean_main_message_collector);
		if (result.isOk() && result.getObject() instanceof MessagesCollector)
			messagesCollector = (MessagesCollector) result.getObject();
		else {
			inMessage.setError("Could not initialize "
					+ Constants._bean_main_message_collector + " object");
			resultList.add(inMessage);
			return resultList.toArray();
		}
		// Send message to default pipe
		getLog().debug("Send message to main input pipe...");
		BeansUtils.getWebContextBean(result, Constants._bean_main_input_pipe);
		if (result.isOk() && result.getObject() instanceof Pipe) {
			((Pipe) result.getObject()).setMessage(inMessage);
		} else {
			getLog().fatal(
					"Could not initialize " 
						+ Constants._bean_main_input_pipe
						+ " object");
			inMessage.setError("Could not initialize "
					+ Constants._bean_main_input_pipe + " object");

			resultList.add(inMessage);
			return resultList.toArray();
		}
		// Setup waiting condition values
		long startTime = System.currentTimeMillis();
		// Waiting for response
		getLog().debug("START: Waiting...");
		while (!messagesCollector.hasNewMessages(inMessage.getSessionID())) {
			// if timeout
			if (System.currentTimeMillis() - startTime > inMessage.getTimeout()) {

				inMessage.setError("Waiting time limit is over...");
				getLog().debug("Waiting time limit is over...");
				inMessage.setError("ERROR: timeout: " + (inMessage.getTimeout()/1000) + " seconds!");

				resultList.add(inMessage);
				return resultList.toArray();
			}
			Thread.yield();
		}
		getLog().debug("... we have new message ...");
		getLog().debug("END: Waiting...");
		// If response messages exist
		IMessage messageBean = null;
		while ((messageBean = messagesCollector.getMessage(inMessage.getSessionID())) != null)
		{
			if(messageBean.getData() != null) messageBean.getData().clear();
			if(messageBean.getFile() != null) messageBean.setFile(null);
			resultList.add((MessageBean) messageBean);
		}
		return resultList.toArray();
	}

	private boolean handledWithSession(MessageBean inMessage,
			WebContext webContext) {
		String handler = inMessage.getData().get("_handler");
		String action = inMessage.getData().get("_action");
		if(handler.compareToIgnoreCase("General") == 0){
			if(action.compareToIgnoreCase("changeLocale") == 0){
				inMessage = (MessageBean) General.changeLocale(inMessage, webContext);
				return(true);
			} else if(action.compareToIgnoreCase("getInitialBody") == 0){
				inMessage = (MessageBean) General.getInitialBody(inMessage, webContext);
				return(true);
			}
		}
		return false;
	}

	public static IMessage getCassandraConfiguration(CommonMessage inMessage, WebContext inContext){
		String result = Utils.T("template.table.with.class",
				"table.name.value",
				String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Server", inContext.getServletContext().getServerInfo())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Protocol", inContext.getHttpServletRequest().getProtocol())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Server Name", inContext.getHttpServletRequest().getServerName())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Server Port", inContext.getHttpServletRequest().getServerPort())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Web Applicication ID", inMessage.getData().get("_appid"))
	
				);
		inMessage.setHtmlContent(result);
		return inMessage;
	}
}
