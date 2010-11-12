package org.hydra.beans.abstracts;

import org.hydra.beans.StatisticsCollector;
import org.hydra.beans.StatisticsCollector.StatisticsTypes;
import org.hydra.beans.interfaces.IStatisticsApplyer;
import org.hydra.utils.abstracts.ALogger;

public abstract class AStatisticsApplyer extends ALogger implements IStatisticsApplyer {
	private StatisticsCollector _statictics = null;

	@Override
	public void setStatistics(String inObjectName, StatisticsTypes inStatType) {
		if(isValidStatistics())
			_statictics.setStatistics(inObjectName, inStatType);
	}

	@Override
	public StatisticsCollector getStaticticsCollector() {
		return _statictics;
	}

	@Override
	public boolean isValidStatistics() {
		return _statictics != null;
	}

	@Override
	public void setStatisticsCollector(StatisticsCollector inStatisticsObject) {
		_statictics = inStatisticsObject;
	}
	


}
