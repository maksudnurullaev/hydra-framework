package org.hydra.services.remote;

import java.util.ArrayList;
import java.util.List;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.services.WebMessagesHandler;
import org.hydra.services.remote.interfaces.IMessageService;
import org.hydra.utils.abstracts.ALogger;

public class MessageService extends ALogger implements IMessageService {

	@Override
	public IMessage[] processMessage(IMessage inMessage) {
		List<IMessage> result = new ArrayList<IMessage>();
		WebMessagesHandler handler = new WebMessagesHandler();
		try {
			handler.handleMessage(inMessage);
			getLog().warn("Remote RMI works!");
			result.add(inMessage);
		} catch (RichedMaxCapacityException e) {
			inMessage.setError(e.getMessage());
			result.add(inMessage);
		}
		getLog().debug("Get new message with session id: " + inMessage.getSessionID());
		return (result.toArray(new IMessage[0]));
	}

}
