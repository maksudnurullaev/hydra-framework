package org.hydra.collectors.interfaces;

import org.hydra.collectors.StatisticsCollector.StatisticsTypes;

public interface IStatisticsCollector {
	public void setStatistics(String inObjectName, StatisticsTypes inStatType);
	public String getTxtReport();
}