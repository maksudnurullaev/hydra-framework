package org.hydra.aspectj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.executors.interfaces.IExecutor;
import org.hydra.processors.abstracts.AProcessor;

public aspect ExecutorWatch {
	Log _log = LogFactory.getLog(this.getClass());

	pointcut newThread(IExecutor inExecutor, Runnable inRunnable) : call(void IExecutor.execute(Runnable))
		&& target(inExecutor)
		&& args(inRunnable);

	before(IExecutor inExecutor, Runnable inRunnable) : newThread(inExecutor, inRunnable) {
		_log.debug(String.format(
								"ApectJ: before IExecutor.execute - Processor(%s) submitted by Executor(%s)",
								((AProcessor) inRunnable).getName(), inExecutor.getName()));
	}

	after(IExecutor inExecutor, Runnable inRunnable) returning(): newThread(inExecutor, inRunnable) {
		_log.debug(String.format(
								"ApectJ: after IExecutor.execute - Processor(%s) submitted by Executor(%s)",
								((AProcessor) inRunnable).getName(), inExecutor.getName()));

	}

}
