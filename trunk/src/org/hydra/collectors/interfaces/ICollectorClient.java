package org.hydra.collectors.interfaces;

public interface ICollectorClient {
	/**
	 * Set collector of current processor
	 * @param inCollector
	 */
	void setMessageCollector(ICollector inCollector);
	/**
	 * Get ICollector object for current processor
	 * @return ICollector
	 */
	ICollector getMessageCollector();
}
