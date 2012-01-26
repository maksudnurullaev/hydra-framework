package org.hydra.services.remote.interfaces;

import org.hydra.messages.interfaces.IMessage;

public interface IMessageService {
	public IMessage[] processMessage(IMessage inMessage);
}
