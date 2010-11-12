package org.hydra.beans.interfaces;

import org.hydra.beans.StatisticsCollector;
import org.hydra.beans.StatisticsCollector.StatisticsTypes;


public interface IStatisticsApplyer {
	public void setStatisticsCollector(StatisticsCollector inStatisticsObject);
	public StatisticsCollector getStaticticsCollector();
	public void setStatistics(String inObjectName, StatisticsTypes inStatType);
	public boolean isValidStatistics();
}
