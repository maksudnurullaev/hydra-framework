package org.hydra.messages.handlers;

import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;

public class RunTestsMessageHandler extends AMessageHandler { // NO_UCD
	public IMessage SessionInfo(IMessage inMessage){
		System.out.println("SessionInfo - called!");
		_log.debug("SessionInfo - called!");
		return inMessage;
	}
}
