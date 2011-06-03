package org.hydra.pipes.abstracts;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.event.EventListenerList;

import org.hydra.beans.StatisticsCollector.StatisticsTypes;
import org.hydra.beans.abstracts.AStatisticsApplyer;
import org.hydra.events.IPipeEventListener;
import org.hydra.events.PipeEvent;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.pipes.interfaces.IPipe;
import org.hydra.processors.exceptions.NullProcessorException;
import org.hydra.processors.interfaces.IProcessor;
import org.hydra.utils.Constants;

/**
 * @author M.Nurullayev
 *
 */
public abstract class APipe extends AStatisticsApplyer implements IPipe<IMessage> {

	private int _maxCapacity = Constants._unlimited;
	private volatile LinkedList<IMessage> _stack = null;
	private String _name = null;
	
	
	//### EVENTS PART
	private EventListenerList _listenerList = new EventListenerList();

	public void addPipeEventListener(IPipeEventListener inListener){
		_listenerList.add(IPipeEventListener.class, inListener);	
		getLog().debug(String.format("Lestener(%s) defined for pipe(%s), count %d", ((IProcessor)inListener).getName(), getName() , _listenerList.getListenerList().length/2));
	}
	
	private void firePipeEvent(PipeEvent evt){
		Object[] listeners = _listenerList.getListenerList();
		
		getLog().debug(String.format("Pipe(%s) has %d listeners", getName(), listeners.length/2));
		
		for (int i = 0; i < listeners.length; i+=2) {
			if(listeners[i] == IPipeEventListener.class){
				getLog().debug(String.format("Wakeup event fired from pipe(%s) for processor(%s)",this.getName(), ((IProcessor)listeners[i+1]).getName()));
				((IPipeEventListener)listeners[i+1]).eventHandleIncomingMessage(evt);
			}
		}
	}
	
	public synchronized int getMaxCapacity() {
		return _maxCapacity;
	}
	
	public synchronized IMessage getMessage() {
		try {
			return getStack().removeFirst();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	public int getMessagesCount() {
		return getStack().size();
	}
	
	public String getName() {
		return _name;
	}

	public int getSize() {
		return getStack().size();
	}

	public LinkedList<IMessage> getStack() {
		if(_stack == null){
			synchronized(this){
				if(_stack == null){
					_stack =  new LinkedList<IMessage>();
				}
			}		
		}
		return _stack;
	}

	public synchronized boolean isEmpty() {
		return getStack().size() == 0;
	}

	public void removePipeEventListener(IPipeEventListener inListener){
		_listenerList.remove(IPipeEventListener.class, inListener);		
	}

	public synchronized void setMaxCapacity(int inCapacity) {
		_maxCapacity = inCapacity;
	}

	public synchronized void setMessage(IMessage inMessage) throws RichedMaxCapacityException  {		
		if(_maxCapacity != Constants._unlimited &&
				_maxCapacity < getStack().size()){ 
			setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
			throw new RichedMaxCapacityException();
		} else {
			getStack().addLast(inMessage);
			setStatistics(getName(), StatisticsTypes.ACCEPTED);
		}
		
		
		getLog().debug(String.format("New message with groupID(%s) added to pipe(%s)", 
				inMessage.getSessionID(), 
				getName()));
		
		// Event part
		if((getStack().size() == 1) && (listenerCount() > 0)){
			getLog().debug(String.format("Firing new Pipe Event from Pipe(%s)", getName()));
			firePipeEvent(new PipeEvent(this));
			getLog().debug("New Pipe Event fired");
		}

	}

	private int listenerCount() {
		return _listenerList.getListenerList().length;
	}
	
	public void setName(String inName) {
		_name = inName;
		getLog().debug(String.format("Pipe set name to '%s'", inName));
	}

	public int getListenerCount() {
		return _listenerList.getListenerCount();
	}

	public void setProcessors(List<IProcessor> inProcessors) throws NullProcessorException {
		for (IProcessor processor : inProcessors) setProcessor(processor);
	}

	public void setProcessor(IProcessor inProcessor) throws NullProcessorException {
		if(inProcessor == null) throw new NullProcessorException();
		addPipeEventListener(inProcessor);
		inProcessor.setInPipe(this);
	}
	
}

