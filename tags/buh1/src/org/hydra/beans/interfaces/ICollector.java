package org.hydra.beans.interfaces;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.interfaces.IPipe;

public interface ICollector {		
	/**
	 * Get name
	 * @return String
	 */
	public String getName();
	/**
	 * Set name
	 */
	public void setName(String inName);	
	/**
	 * Get messages for certain session 
	 * (here key could be session id)
	 * @param key
	 * @return
	 */
	IMessage getMessage(String key);
	
	/**
	 * Set message for certain session
	 * @param inMessage
	 */
	void putMessage(IMessage inMessage);
	
	/**
	 * Get/Create Messages Pipe for session
	 * @param key
	 * @return
	 */
	IPipe<IMessage> getPipe(String key);
	
				
	
	/**
	 * Define that collector has new messages for user session 
	 * @param key
	 * @return boolean
	 */
	boolean hasNewMessages(String key);
	void removePipe(String key);
	int getTotalMessageCount();
	
	

	
}
