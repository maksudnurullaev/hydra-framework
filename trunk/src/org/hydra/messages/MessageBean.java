package org.hydra.messages;

import java.util.HashMap;

import org.hydra.messages.abstracts.AMessage;

public class MessageBean extends AMessage {

	public void setSessionID(String currentSessionID) {
		if(getData() == null) setData(new HashMap<String, String>());
		getData().put(_data_sessionId, currentSessionID);
	}
}

