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
	 * Removes Message Pipe for certain session
	 * (i.e. for dormant sessions)
	 * @param key
	 */
	void removePipe(String key);			
	
	/**
	 * Define that collector has new messages for user session 
	 * @param key
	 * @return boolean
	 */
	boolean hasNewMessages(String key);
	
	/**
	 * Get total accepted message count for collector 
	 * @return int
	 */	
	int getTotalMessageCount();
}
