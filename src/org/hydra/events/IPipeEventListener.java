package org.hydra.events;

import java.util.EventListener;

public interface IPipeEventListener extends EventListener {
	/**
	 * Method to rise event
	 * @param evt
	 * @throws NullExecutorException 
	 */
	public void eventHandleIncomingMessage(PipeEvent evt);

}
