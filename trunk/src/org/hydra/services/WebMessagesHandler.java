package org.hydra.services;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.WebContextFactory;
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

	public Object[] sendMessage(MessageBean inMessage)
			throws RichedMaxCapacityException {
		// return result messages array
		List<MessageBean> _return_result = new ArrayList<MessageBean>();
		// set message collector
		MessagesCollector messagesCollector = null;
		Result result = BeansUtils
				.getWebSessionBean(Constants._beans_main_message_collector);
		if (result.isOk() && result.getObject() instanceof MessagesCollector)
			messagesCollector = (MessagesCollector) result.getObject();
		else {
			inMessage.setError("Could not initialize "
					+ Constants._beans_main_message_collector + " object");
			_return_result.add(inMessage);
			return _return_result.toArray();
		}
		// Attach session's data
		result = SessionUtils
				.setSessionData(inMessage, WebContextFactory.get());
		if (!result.isOk()) {
			inMessage.setError(result.getResult());
			SessionUtils.removeSessionData(inMessage);
			_return_result.add(inMessage);
			return _return_result.toArray();
		}
		// Send message to default pipe
		getLog().debug("Send message to default pipe...");
		result = BeansUtils.getWebSessionBean(Constants._beans_main_input_pipe);
		if (result.isOk() && result.getObject() instanceof Pipe) {
			((Pipe) result.getObject()).setMessage(inMessage);
		} else {
			getLog().fatal(
					"Could not initialize " + Constants._beans_main_input_pipe
							+ " object");
			inMessage.setError("Could not initialize "
					+ Constants._beans_main_input_pipe + " object");

			SessionUtils.removeSessionData(inMessage);
			_return_result.add(inMessage);
			return _return_result.toArray();
		}
		// Setup waiting condition values
		long startTime = System.currentTimeMillis();
		// Waiting for response
		while (!messagesCollector.hasNewMessages(inMessage.getData().get(inMessage._session_id))) {
			// if timeout
			if (System.currentTimeMillis() - startTime > Constants._max_response_wating_time) {

				inMessage.setError("Waiting time limit is over...");
				getLog().error("Waiting time limit is over...");

				SessionUtils.removeSessionData(inMessage);
				_return_result.add(inMessage);
				return _return_result.toArray();
			}
			Thread.yield();
		}
		getLog().debug("END: Waiting for response...");
		// If response messages exist
		IMessage messageBean = null;
		while ((messageBean = messagesCollector.getMessage(inMessage.getData()
				.get(inMessage._session_id))) != null) {

			SessionUtils.removeSessionData(messageBean);
			_return_result.add((MessageBean) messageBean);
		}
		return _return_result.toArray();
	}

}
