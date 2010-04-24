package org.hydra.tests.utils;

import org.hydra.collectors.StatisticsCollector;
import org.hydra.collectors.StatisticsCollector.StatisticsTypes;

public class Just4Statistics {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StatisticsCollector stat = new StatisticsCollector();
		stat.setStatistics("TestObject1", StatisticsTypes.ACCEPTED);
		stat.setStatistics("TestObject1", StatisticsTypes.ACCEPTED);
		stat.setStatistics("TestObject3", StatisticsTypes.ACCEPTED);
		stat.setStatistics("TestObject1", StatisticsTypes.BYPASSED);
		stat.setStatistics("TestObject2", StatisticsTypes.WITH_ERRORS);
		stat.setStatistics("TestObject2", StatisticsTypes.WITH_ERRORS);
		System.out.print(stat.getTxtReport());
	}

}
