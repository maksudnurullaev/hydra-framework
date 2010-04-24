package org.hydra.collectors.interfaces;

import org.hydra.collectors.StatisticsCollector;
import org.hydra.collectors.StatisticsCollector.StatisticsTypes;

public interface IStatisticsApplyer {
	public void setStatisticsCollector(StatisticsCollector inStatisticsObject);
	public StatisticsCollector getStaticticsCollector();
	public void setStatistics(String inObjectName, StatisticsTypes inStatType);
	public boolean isValidStatistics();
}
