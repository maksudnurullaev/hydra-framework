package org.hydra.executors.interfaces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface IExecutor {

	public abstract void execute(Runnable inProcessor);

	public abstract boolean isValidPool();

	public abstract void setName(String _name);

	public abstract String getName();

	public abstract void awaitTermination(long inTimeOut, TimeUnit inTimeUnit);

	ExecutorService getThreadsPool();

	void shutdownAndAwaitTermination(long inTimeOut, TimeUnit inTimeUnit);
}