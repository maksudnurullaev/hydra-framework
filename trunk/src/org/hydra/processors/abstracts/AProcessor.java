package org.hydra.processors.abstracts;

import java.lang.reflect.Method;

import org.hydra.beans.StatisticsCollector.StatisticsTypes;
import org.hydra.beans.abstracts.AStatisticsApplyer;
import org.hydra.beans.interfaces.ICollector;
import org.hydra.events.PipeEvent;
import org.hydra.executors.interfaces.IExecutor;
import org.hydra.messages.handlers.intefaces.IMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.pipes.interfaces.IPipe;
import org.hydra.processors.exceptions.NullPipeException;
import org.hydra.processors.interfaces.IProcessor;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

public abstract class AProcessor extends AStatisticsApplyer implements
		IProcessor {

	// Default values
	private IPipe<IMessage> _logPipe = null;
	private IPipe<IMessage> _inPipe = null;
	private IPipe<IMessage> _outPipe = null;
	private String _name = null;
	private ProcessorStatus _myState = ProcessorStatus.WAITING;
	private IExecutor _executor = null;
	private ICollector _messagesCollector = null;

	@Override
	public ICollector getMessageCollector() {
		return _messagesCollector;
	}

	@Override
	public void setMessageCollector(ICollector inCollector) {
		_messagesCollector = inCollector;
		getLog().debug(
				String.format("%s set message collector to %s", getName(),
						inCollector
								.getName()));
	}

	public Result getMessageHandler(IMessage inMessage) {
		// Spring Debug Mode
		if (AppContext.isDebugMode()) {
			trace = Constants.trace(this, Thread.currentThread()
					.getStackTrace());
		} else
			trace = "";

		Result _result = new Result();
		String handlerName = inMessage.getData().get(IMessage._handler_id);
		String path2MessageHandler = Constants._message_handler_class_prefix
					+ handlerName;

		getLog().debug(
				String.format("Default handler for message group(%s) is %s "
						, inMessage.getData().get(IMessage._data_sessionId)
						, path2MessageHandler));

		getLog().debug("Try to create instance of " + path2MessageHandler);
		try {
			Object object = Class.forName(path2MessageHandler).newInstance();
			getLog().debug(
					String.format("Instance of (%s) created successfully!",
							path2MessageHandler));
			_result.setResult(true);
			_result.setObject(object);

		} catch (Exception e) {
			String errorStr = "Error: " + e.toString();
			_result.setResult(false);
			_result.setResult(errorStr);
			getLog().error(e.toString());
		}
		return _result;
	}

	public void setInPipe(IPipe<IMessage> inPipe) {
		this._inPipe = inPipe;
		getLog().debug(
				String.format("InPipe(%s) defined for processor(%s)", inPipe
						.getName(), getName()));
	}

	public IPipe<IMessage> getInPipe() {
		return _inPipe;
	}

	@Override
	public void setOutPipe(IPipe<IMessage> inPipe) {
		this._outPipe = inPipe;
	}

	public IPipe<IMessage> getOutpipe() {
		return _outPipe;
	}

	public boolean isValidDefaultInPipe() {
		return (_inPipe != null);
	}

	public boolean isValidDefaultOutPipe() {
		return (_outPipe != null);
	}

	public void setName(String inName) {
		this._name = inName;
		getLog().debug(String.format("Processor set name to '%s'", inName));
	}

	public String getName() {
		return _name;
	}

	public void setLogPipe(IPipe<IMessage> _logPipe) {
		this._logPipe = _logPipe;
	}

	public IPipe<IMessage> getLogPipe() {
		return _logPipe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.supposition.events.IPipeEventListener#pipeHasMessage(org.supposition
	 * .events.PipeEvent)
	 */
	@Override
	public void eventHandleIncomingMessage(PipeEvent evt) {
		getLog().debug(
				String.format("Processor(%s) has new PipeEvent", getName()));
		if (getState() == ProcessorStatus.WAITING) {
			if (isValidExecutor()) {
				_executor.execute(this);
				getLog().debug(
						String.format(
								"Processor(%s) submited by handle new messages from Pipe's event",
								getName()));
			} else {
				getLog().fatal("Executor is NULL");
				System.exit(1);
			}
		}
	}

	private boolean isValidExecutor() {
		return (_executor != null);
	}

	public IExecutor getExecutor() {
		return _executor;
	}

	public void setExecutor(IExecutor inExecutor) {
		_executor = inExecutor;
	}

	/**
	 * @return the myState
	 */
	public synchronized ProcessorStatus getState() {
		return _myState;
	}

	/**
	 * @param myState
	 *            the myState to set
	 */
	public synchronized void setState(ProcessorStatus myState) {
		_myState = myState;
	}

	public void sendToOutPipe(IMessage message) {
		if (isValidDefaultOutPipe()) {
			try {
				getOutpipe().setMessage(message);
			} catch (RichedMaxCapacityException e) {
				getLog().error(String.format(
						"Pipe(%s) riched maximal capacity(%d)", getOutpipe()
								.getName(), getOutpipe().getMaxCapacity()));
			}
		} else {
			getLog().debug("Invalid OutPipe for: " + getName());
		}
	}

	@Override
	public void run() {
		// Start
		// **** Change thread state
		setState(ProcessorStatus.WORKING);
		// ****

		// 1. Main Validations
		// 1.1 Incoming Pipe
		if (getInPipe() == null)
			try {
				throw new NullPipeException();
			} catch (NullPipeException e) {
				e.printStackTrace();
			}

		IMessage message = null;
		getLog().debug(String.format("Start to handle pipe(%s) message(s)",
				getInPipe().getName()));
		while ((message = (IMessage) getInPipe().getMessage()) != null) {
			// Log message
			getLog().debug(String.format(
					"Processor(%s) handle message for group(%s)", getName(),
					message.getData().get(IMessage._data_sessionId)));

			if (isExpectedMessage(message)) {
				applyMessage(message);
			} else
				sendToOutPipe(message);

		}
		// Stop
		// **** Change thread state
		setState(ProcessorStatus.WAITING);
		// ****
	}

	public void applyMessage(IMessage inMessage) {
		getLog().debug(
				String.format("Handle new message for group(%s)...", inMessage
						.getData().get(IMessage._data_sessionId)));

		String handlerName = inMessage.getData().get(IMessage._handler_id);
		String actionName = inMessage.getData().get(IMessage._action_id);

		_log.debug("check for valid handler and action");
		if (Utils.isInvalidString(handlerName)) {
			setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
			inMessage.setError("Null or empty message's handler!");
			getMessageCollector().putMessage(inMessage);
		} else if (Utils.isInvalidString(actionName)) {
			setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
			inMessage.setError("Null or empty message handler's action!");
			getMessageCollector().putMessage(inMessage);
		} else {

			String path2MessageHandler = Constants._message_handler_class_prefix
					+ handlerName;

			try {
				_log.debug("try to handle message by action");
				Class<?> c = Class.forName(path2MessageHandler);
				Class<?> parameterTypes[] = new Class[1];
				parameterTypes[0] = IMessage.class;
				Method m = c.getMethod(actionName, parameterTypes);
				Object result = m.invoke(c.newInstance(), inMessage);
				if (result instanceof IMessage) {
					setStatistics(getName(), StatisticsTypes.ACCEPTED);
					getMessageCollector().putMessage((IMessage) result);
				} else {
					setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
					inMessage.setError("Result is not instance of IMessage!");
					getMessageCollector().putMessage(inMessage);
				}
			} catch (Exception e) {
				_log.error(e.toString());
				setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
				inMessage.setError(e.toString());
				getMessageCollector().putMessage(inMessage);
			}
		}
		/*
		 * // *** Get message handler Result _result =
		 * getMessageHandler(inMessage); if(_result.isOk()){
		 * if(_result.getObject() instanceof IMessageHandler){ IMessageHandler
		 * messageHandler = (IMessageHandler) _result.getObject();
		 * 
		 * messageHandler.handleMessage(inMessage);
		 * 
		 * setStatistics(getName(), StatisticsTypes.ACCEPTED);
		 * 
		 * getLog().debug(String.format("Apply new message for groupID(%s)...DONE"
		 * , inMessage.getData().get(IMessage._data_sessionId))); }else{
		 * setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
		 * 
		 * inMessage.setError( String.format(
		 * "MessageHandler(%s) should be inherited from IMessageHandler",
		 * _result.getObject().getClass().getSimpleName())); } }else{
		 * setStatistics(getName(), StatisticsTypes.WITH_ERRORS);
		 * inMessage.setError(_result.getResult()); }
		 */
	}

	@Override
	public boolean isExpectedMessage(IMessage message) {
		return true;
	}
}
