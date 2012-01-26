package org.hydra.services;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.WebContextFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.MessagesCollector;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.abstracts.ALogger;

public class WebMessagesHandler extends ALogger {
	public Object[] sendMessage2(MessageBean inMessage, FileTransfer inFile) throws RichedMaxCapacityException{
		if(inFile != null){
			getLog().debug("inFile.getFilename(): " + inFile.getFilename());
			getLog().debug("inFile.getMimeType(): " + inFile.getMimeType());
			getLog().debug("inFile.getSize(): " + inFile.getSize());
		}else{
			getLog().warn("inFile == null!");			
		}
		inMessage.setFile(inFile);
		return sendMessage(inMessage);
	}
	
	public Object[] sendMessage(MessageBean inMessage)
			throws RichedMaxCapacityException {
		// return result messages array
		List<MessageBean> _return_result = new ArrayList<MessageBean>();
		// set message collector
		MessagesCollector messagesCollector = null;
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._bean_main_message_collector);
		if (result.isOk() && result.getObject() instanceof MessagesCollector)
			messagesCollector = (MessagesCollector) result.getObject();
		else {
			inMessage.setError("Could not initialize "
					+ Constants._bean_main_message_collector + " object");
			_return_result.add(inMessage);
			return _return_result.toArray();
		}
		// Attach session's data
		SessionUtils.attachSessionData(result, inMessage, WebContextFactory.get());
		if (!result.isOk()) {
			inMessage.setError(result.getResult());
			_return_result.add(inMessage);
			return _return_result.toArray();
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

			_return_result.add(inMessage);
			return _return_result.toArray();
		}
		// Setup waiting condition values
		long startTime = System.currentTimeMillis();
		// Waiting for response
		getLog().debug("START: Waiting...");
		while (!messagesCollector.hasNewMessages(inMessage.getSessionID())) {
			// if timeout
			if (System.currentTimeMillis() - startTime > inMessage._web_application.getTimeout()) {

				inMessage.setError("Waiting time limit is over...");
				getLog().debug("Waiting time limit is over...");
				inMessage.setError("ERROR: timeout: " + (inMessage._web_application.getTimeout()/1000) + " seconds!");

				_return_result.add(inMessage);
				return _return_result.toArray();
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
			_return_result.add((MessageBean) messageBean);
		}
		return _return_result.toArray();
	}
}
