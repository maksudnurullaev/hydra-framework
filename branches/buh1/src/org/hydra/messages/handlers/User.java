package org.hydra.messages.handlers;

import org.directwebremoting.WebContext;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;

public class User extends AMessageHandler {
	public static void login(IMessage inMessage, WebContext webContext) {
		
		inMessage.setError("Hello from source!");
	}
}
