package org.hydra.services.remote;

import java.util.ArrayList;
import java.util.List;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.services.remote.interfaces.IMessageService;

public class MessageService implements IMessageService {

	@Override
	public IMessage[] processMessage(IMessage inMessage) {
		inMessage.setError("Remote RMI works!");
		List<IMessage> result = new ArrayList<IMessage>();
		result.add(inMessage);
		return (IMessage[]) (result.toArray());
	}

}
