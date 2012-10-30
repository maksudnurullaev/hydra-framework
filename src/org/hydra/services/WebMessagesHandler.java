package org.hydra.services;

import java.util.ArrayList;
import java.util.List;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.MessagesCollector;
import org.hydra.messages.MessageBean;
import org.hydra.messages.handlers.General;
import org.hydra.messages.handlers.User;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.CaptchaUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.Utils;
import org.hydra.utils.abstracts.ALogger;

public class WebMessagesHandler extends ALogger {
	public Object[] sendMessage(MessageBean inMessage, FileTransfer inFile) throws RichedMaxCapacityException {	
		List<IMessage> resultList = new ArrayList<IMessage>();
		if(!AMessageHandler.validateData(inMessage, "handler", "action")){
			getLog().error("Not valid HANDLER and/or ACTION value!");
			resultList.add(inMessage);
			return(resultList.toArray());
		}
		// Detect web context
		if(WebContextFactory.get() == null){
			getLog().error("WebContext is null!");
			inMessage.setError("WebContext is null!");
			resultList.add(inMessage);
			return(resultList.toArray());
		}
		WebContext webContext = WebContextFactory.get();
		// attach session's data
		Result result = new Result();
		SessionUtils.setApplicationData(result, inMessage, webContext);
		if (!result.isOk()) {
			inMessage.setError(result.getResult());
			resultList.add(inMessage);
			return resultList.toArray();
		}
		
		// ************* handle incoming message ************* //
		
		// test for action with session
		if(handledWithSession(inMessage, webContext)){
			resultList.add(inMessage);
			return resultList.toArray();			
		}		
		// test & setup captcha if needs
		if(!CaptchaUtils.validateIfNeedsCaptcha(inMessage, webContext)){
			CaptchaUtils.makeError4Captcha(inMessage);
			resultList.add(inMessage);
			return resultList.toArray();
		}		
		// test & setup file if needs
		if(inFile != null){
			inMessage.setFile(inFile);
			FileUtils.setupFile(inMessage);
		}
		
		return(handleMessage(inMessage));
	}
	
	public Object[] handleMessage(IMessage inMessage) throws RichedMaxCapacityException {
		List<IMessage> resultList = new ArrayList<IMessage>();
		Result result = new Result();

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
		while (!messagesCollector.hasNewMessages(inMessage.getSessionId())) {
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
		while ((messageBean = messagesCollector.getMessage(inMessage.getSessionId())) != null)
		{
			if(messageBean.getData() != null) messageBean.getData().clear();
			resultList.add((MessageBean) messageBean);
		}
		return resultList.toArray();		
	}

	private boolean handledWithSession(IMessage inMessage,
			WebContext webContext) {
		String handler = Utils.getMessageDataOrNull(inMessage, "handler");
		String action = Utils.getMessageDataOrNull(inMessage, "action");
		if(handler.compareToIgnoreCase("General") == 0){
			if(action.compareToIgnoreCase("changeLocale") == 0){
				General.changeLocale(inMessage, webContext);
				inMessage.setReloadPage(true);
				return(true);
			}
		} else if(handler.compareToIgnoreCase("User") == 0){
			if(action.compareToIgnoreCase("login") == 0){
				User.login(inMessage, webContext);
				return(true);
			} else if(action.compareToIgnoreCase("logout") == 0){
				webContext.getSession().invalidate();
				inMessage.setReloadPage(true);
				return(true);
			}
		}
		//
		return false;
	}
}
