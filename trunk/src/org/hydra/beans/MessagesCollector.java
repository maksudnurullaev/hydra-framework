package org.hydra.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.interfaces.ICollector;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.pipes.interfaces.IPipe;
import org.hydra.utils.Constants;

public class MessagesCollector implements ICollector {
	
	private Log _log = LogFactory.getLog(this.getClass());	
	private int _total_message = 0;
	private Map<String, IPipe<IMessage>> _groupOfMessages = new HashMap<String, IPipe<IMessage>>();
	private String _name = Constants.UnknownString;
	
	@Override
	public IMessage getMessage(String key) {
		return getPipe(key).getMessage();
	}
	
	/**
	 * Simple factory method for new Message Pipe
	 * (could be override by child class)
	 * @return IPipe<IMessage>
	 */
	private IPipe<IMessage> getNewPipe() {		
		return (new Pipe());
	}

	@Override
	public IPipe<IMessage> getPipe(String inGroupID) {
		synchronized (_groupOfMessages) { //TODO [later]... still not sure that is optimal version!?
			if(!_groupOfMessages.containsKey(inGroupID)){
				IPipe<IMessage> newPipe = getNewPipe();
				newPipe.setName(inGroupID);
				_groupOfMessages.put(inGroupID, newPipe);
				_log.debug(String.format("New Collector Pipe (%s) created", inGroupID));
			}	
		}
		return _groupOfMessages.get(inGroupID);
	}

	@Override
	public void removePipe(String key) {
		_groupOfMessages.remove(key);
	}

	@Override
	public void putMessage(IMessage inMessage) {
		try {
			getPipe(inMessage.getSessionID()).setMessage(inMessage);
			_log.debug(String.format("New message(%s) added to pipe(%s), total %d", 
					inMessage.toString(), 
					inMessage.getSessionID(), 
					getPipe(inMessage.getSessionID()).getMessagesCount()));
		} catch (RichedMaxCapacityException e) {
			_log.error(String.format("Could not add message(%s) to group(%s)", 
					inMessage.toString(), 
					inMessage.getSessionID()));
		}
		// Increase message statistics
		synchronized (this) {
			_total_message++;
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String inName) {
		_name = inName;
		
	}

	@Override
	public boolean hasNewMessages(String key) {
		int messageCount = getPipe(key).getMessagesCount();
		_log.debug(String.format("Collector has %d message(s)", messageCount));
		return (messageCount > 0);
	}

	@Override
	public int getTotalMessageCount() {
		synchronized (this) {
			return _total_message;			
		}
	}

}
