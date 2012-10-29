package org.hydra.pipes.interfaces;

import java.util.List;

import org.hydra.events.IPipeEventListener;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.processors.exceptions.NullProcessorException;
import org.hydra.processors.interfaces.IProcessor;

/**
 * Pipe interface to route {@link IMessage} objects to {@link IProcessor}, {@link ICollector} and etc.
 * @author M.Nurullayev
 * @param <E>
 * @see {@link IMessage}, {@link IProcessor}, {@link ICollector}
 */
public interface IPipe<E> {
	/**
	 * Add new listener
	 * @param inListener
	 */
	public void addPipeEventListener(IPipeEventListener inListener);

	/**
	 * Get Maximum capacity
	 * @return int
	 */
	public int getMaxCapacity();

	/**
	 * Get next message from pipe
	 * @return
	 */
	public E getMessage();

	/**
	 * Get messages count in pipe
	 * @return int
	 */
	public int getMessagesCount();

	/**
	 * Get name of pipe
	 * @return String
	 */
	public String getName();

	/**
	 * Get current size of pipe
	 * @return int
	 */
	public int getSize();

	/**
	 * Get current stack of messages
	 * @return List<AMessage>
	 */
	public List<IMessage> getStack();

	/**
	 * Check that pipe has message
	 * @return boolean
	 */
	public boolean isEmpty();

	
	
	/**
	 * Get count of listeners
	 * @return int
	 */
	public int getListenerCount();

	/**
	 * Set maximal capacity
	 * @param inCapacity
	 */
	public void setMaxCapacity(int inCapacity);

	/**
	 * Add new message to pipe
	 * @param inMessage
	 * @throws RichedMaxCapacityException
	 * @throws NullExecutorException 
	 */
	public void setMessage(E inMessage) throws RichedMaxCapacityException;

	/**
	 * Set name of pipe
	 * @param inName
	 */
	public void setName(String inName);
	
	/**
	 * Set new processors to pipe
	 * @param List<IProcessor>
	 * @throws NullProcessorException 
	 */
	public void setProcessors(List<IProcessor> inProcessor) throws NullProcessorException;

	/**
	 * Set new processor to pipe
	 * @param IProcessor
	 * @throws NullProcessorException 
	 */
	public void setProcessor(IProcessor inProcessor) throws NullProcessorException;
	
}
