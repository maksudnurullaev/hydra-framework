package org.hydra.processors.interfaces;

import org.hydra.beans.interfaces.ICollector;
import org.hydra.events.IPipeEventListener;
import org.hydra.events.PipeEvent;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.interfaces.IPipe;

public interface IProcessor extends Runnable, IPipeEventListener {
	/**
	 * Simple Processor state enum, descibe th thread state: <li>(WORKING) -
	 * hadling message <li>(WAITING) - waitng for event
	 * 
	 * @see #eventHandleIncomingMessage(PipeEvent)
	 * @author M.Nurullayev
	 */
	public enum ProcessorStatus {
		WORKING, WAITING
	};

	/**
	 * get state of processor
	 * 
	 * @param inState
	 */
	public void setState(ProcessorStatus inState);

	/**
	 * Get state of processor
	 * 
	 * @return STATE
	 */
	public ProcessorStatus getState();

	/**
	 * Returns incoming pipe
	 * 
	 * @return IPipe
	 */
	IPipe<IMessage> getInPipe();

	/**
	 * Get processor name
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Get outgoing pipe
	 * 
	 * @return IPipe
	 */
	IPipe<IMessage> getOutpipe();

	
	
	/**
	 * Define that processor has valid default outgoing pipe
	 * @return boolean
	 */
	boolean isValidDefaultOutPipe();
	
	
	/**
	 * Set incoming pipe
	 * @param IPipe
	 */
	void setInPipe(IPipe<IMessage> inPipe);
		
	/**
	 * Set name of processor
	 * @param String
	 */
	void setName(String inName);
	
	/**
	 * Set outcoming pipe
	 * @param IPipe
	 */
	void setOutPipe(IPipe<IMessage> inPipe);
	
	/**
	 * Send message to outgoing pipe
	 * @param message
	 * @throws NullExecutorException 
	 */
	void sendToOutPipe(IMessage message);
	
	/**
	 * Get linked message collector
	 * @return {@link ICollector}
	 */
	ICollector getMessageCollector();

	/**
	 * Set up message collector
	 * @param inCollector
	 */
	void setMessageCollector(ICollector inCollector);
	
	/**
	 * Defines, is message is expectable or should be bypassed
	 * @param inMessage
	 * @return boolean
	 */
	abstract boolean isExpectedMessage(IMessage inMessage);	
}
