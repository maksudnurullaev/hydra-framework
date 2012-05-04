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
		// test for action with session
		if(handledWithSession(inMessage, webContext)){
			resultList.add(inMessage);
			return resultList.toArray();			
		}		
		// test & setup captcha if needs
		if(inMessage.getData().containsKey(Constants._captcha_value)
				&& !CaptchaUtils.validateCaptcha(inMessage, webContext)){
			CaptchaUtils.makeCaptchaNotVerifiedMessage(inMessage);
			resultList.add(inMessage);
			return resultList.toArray();
		}		
		// test & setup file if needs
		if(inFile != null){
			inMessage.setFile(inFile);
			setupFile(inMessage);
		}
		//TODO just for test SessionUtils.printSessionData(webContext, inMessage);

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
		while (!messagesCollector.hasNewMessages(inMessage.getSession().getId())) {
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
		while ((messageBean = messagesCollector.getMessage(inMessage.getSession().getId())) != null)
		{
			if(messageBean.getData() != null) messageBean.getData().clear();
			resultList.add((MessageBean) messageBean);
		}
		return resultList.toArray();		
	}

	private void setupFile(IMessage inMessage) {
		getLog().debug("File name/size: " + inMessage.getFile().getFilename() + "/" + inMessage.getFile().getSize());
		String appId = inMessage.getData().get("appid");
		String folder = inMessage.getData().get("folder");
		String uri4File = Utils.F(FileUtils.URL4FILES_APPID_SUBFOLDER, appId, folder) + FileUtils.sanitize(inMessage.getFile().getFilename());
		inMessage.getData().put("file_path", uri4File);
		_log.debug("File uri: " + uri4File);
		inMessage.getData().put("file_real_path", FileUtils.getRealFile(uri4File).getPath());
		_log.debug("Real path: " + inMessage.getData().get("file_real_path"));
	}

	private boolean handledWithSession(IMessage inMessage,
			WebContext webContext) {
		String handler = inMessage.getData().get("handler");
		String action = inMessage.getData().get("action");
		if(handler.compareToIgnoreCase("General") == 0){
			if(action.compareToIgnoreCase("changeLocale") == 0){
				General.changeLocale(inMessage, webContext);
				
				if(inMessage.getError() != null) return(true);
				
				// send message to initialize body as usual
				inMessage.getData().put("handler", "General");				
				inMessage.getData().put("action", "getInitialBody");
				return(false);
			}
		} else if(handler.compareToIgnoreCase("User") == 0){
			if(action.compareToIgnoreCase("login") == 0){
				inMessage = (MessageBean) User.login(inMessage, webContext);
				return(true);
			} else if(action.compareToIgnoreCase("logout") == 0){
				inMessage = (MessageBean) User.logout(inMessage, webContext);
				return(true);
			}
		}else if(handler.compareToIgnoreCase("Adm") == 0){
			if(action.compareToIgnoreCase("getCassandraConfiguration") == 0){
				inMessage = (MessageBean) getCassandraConfiguration(inMessage, webContext);
				return(true);
			} 
		}
		//
		return false;
	}

	public static IMessage getCassandraConfiguration(IMessage inMessage, WebContext inContext){
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
						"Web Applicication ID", inMessage.getData().get("appid"))
				);
		inMessage.setHtmlContent(result);
		return inMessage;
	}
}
