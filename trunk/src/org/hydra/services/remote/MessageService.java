package org.hydra.services.remote;

import java.util.ArrayList;
import java.util.List;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.services.remote.interfaces.IMessageService;
import org.hydra.utils.abstracts.ALogger;

public class MessageService extends ALogger implements IMessageService {

	@Override
	public IMessage[] processMessage(IMessage inMessage) {
		getLog().debug("Get new message with session id: " + inMessage.getSessionID());
		inMessage.setError("Remote RMI works!");
		List<IMessage> result = new ArrayList<IMessage>();
		result.add(inMessage);
		return (result.toArray(new IMessage[0]));
	}

}
