package org.hydra.beans.interfaces;

import org.hydra.beans.StatisticsCollector.StatisticsTypes;


public interface IStatisticsCollector {
	public void setStatistics(String inObjectName, StatisticsTypes inStatType);
	public String getTxtReport();
}