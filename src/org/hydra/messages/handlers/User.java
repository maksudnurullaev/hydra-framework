package org.hydra.messages.handlers;

import org.directwebremoting.WebContext;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

public class User extends AMessageHandler {
	public static void login(IMessage inMessage, WebContext webContext) {
		Utils.dumpIncomingWebMessage(inMessage);
		inMessage.setError("Hello from source!");
	}
}
