package org.hydra.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hydra.executors.interfaces.IExecutor;
import org.hydra.processors.interfaces.IProcessor;
import org.hydra.utils.abstracts.ALogger;

public class Executor extends ALogger implements IExecutor {
	private static final ExecutorService _pool = Executors.newCachedThreadPool();
	private static final Executor _self = new Executor();
	private String _name = "Name is not initilazed!";

	private Executor() {
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.supposition.executors.IExecutor#execute(java.lang.Runnable)
	 */
	public void execute(Runnable inProcessor) {
		if(inProcessor instanceof IProcessor){
			getLog().debug(String.format("New processor(%s) submitted to execute.",((IProcessor) inProcessor).getName()));
		}
		_pool.execute(inProcessor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.supposition.executors.IExecutor#isValidPool()
	 */
	public boolean isValidPool() {
		return (_pool != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.supposition.executors.IExecutor#setName(java.lang.String)
	 */
	public void setName(String _name) {
		this._name = _name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.supposition.executors.IExecutor#getName()
	 */
	public String getName() {
		return _name;
	}

	public static Executor getInstance() {
		if(_self == null)
			throw new NullPointerException("SimpleExecutor is NULL") ;
		return _self;
	}

	@Override
	public void shutdownAndAwaitTermination(long inTimeOut, TimeUnit inTimeUnit) {		
		_pool.shutdown(); // Disable new tasks from being submitted
		awaitTermination(inTimeOut, inTimeUnit);
	}

	@Override
	public void awaitTermination(long inTimeOut, TimeUnit inTimeUnit) {
		try {
			// Wait a while for existing tasks to terminate
			if (!_pool.awaitTermination(inTimeOut, inTimeUnit)) {
				_pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!_pool.awaitTermination(inTimeOut, inTimeUnit))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			_pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	public ExecutorService getThreadsPool(){
		return _pool;
	}
	
}
