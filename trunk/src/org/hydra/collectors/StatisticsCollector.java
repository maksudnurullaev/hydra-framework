package org.hydra.collectors;

import java.util.HashMap;
import java.util.Map;

import org.hydra.collectors.interfaces.IStatisticsCollector;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.abstracts.ALogger;

public class StatisticsCollector extends ALogger implements IStatisticsCollector {
	public enum StatisticsTypes{ACCEPTED, BYPASSED, WITH_ERRORS};
	private Map<String, Map<StatisticsTypes,Integer>> _statistics = new HashMap<String, Map<StatisticsTypes,Integer>>();

	
	public void setStatistics(String inObjectName, StatisticsTypes inStatType){
		if(!_statistics.containsKey(inObjectName)){
			Map<StatisticsTypes,Integer> newMap = newBlankMap();
			_statistics.put(inObjectName, newMap);
			getLog().debug("Created new statistics map for: " + inObjectName);
		}
		
		_statistics.get(inObjectName).put(inStatType, (_statistics.get(inObjectName).get(inStatType) + 1));			
		
		getLog().debug(String.format("Fixed %s statistics type for: %s", inStatType.toString(), inObjectName));
	}

	private Map<StatisticsTypes,Integer> newBlankMap() {
		Map<StatisticsTypes,Integer> newMap = new HashMap<StatisticsTypes,Integer>();
		for (StatisticsTypes type : StatisticsTypes.values()) {
			newMap.put(type, new Integer(0));
		}
		return newMap;
	}
	
	public String getTxtReport(){
		StringBuffer result = new StringBuffer("\n");
		Map<StatisticsTypes,Integer> totals = newBlankMap();
		
		String mainFormat = "%14s |";
		String columnLine = "================"; //"%14s |" = 16 chars
		
		getLog().debug("Start to generate statistics report!");
		
		// Table header
		result.append(String.format(mainFormat, "Statistics"));
		for (StatisticsTypes type : StatisticsTypes.values()) {
			result.append(String.format(mainFormat, type.toString()));
		}
		result.append("\n");
		
		// Table header line
		for (int i=0; i <= StatisticsTypes.values().length; i++) {
			result.append(columnLine);
		}
		result.append("\n");
		
		for (Map.Entry<String, Map<StatisticsTypes,Integer>> mapStringTypeInteger : _statistics.entrySet()) {
			result.append(String.format(mainFormat, mapStringTypeInteger.getKey()));
			for (StatisticsTypes type : StatisticsTypes.values()) {
				result.append(String.format(mainFormat, mapStringTypeInteger.getValue().get(type)));
				// Prepare values for table footers
				totals.put(type, totals.get(type) + mapStringTypeInteger.getValue().get(type));
			}
			result.append("\n");
		}
		
		return result.toString();
	}

	public int getMessagesStatistics4(String inObjectName, StatisticsTypes inStatisticsType) {
		if(!_statistics.containsKey(inObjectName)) return -1;
		return _statistics.get(inObjectName).get(inStatisticsType);
	}

	public int getMessagesTotal4(String inObjectName) {
		if(!_statistics.containsKey(inObjectName)) return -1;
		
		int result = 0;
		
		for (StatisticsTypes type : StatisticsTypes.values()) {
			result += _statistics.get(inObjectName).get(type);
		}
		
		return result;
	}

	public String getHtmlReport() {
		String result = ""; 
		String tempString = "";
		
		// Table header
		tempString += String.format(MessagesManager.getTextManager().getTextByKey("template.table.th", null), "&nbsp;");
		for (StatisticsTypes type : StatisticsTypes.values()) {
			tempString += String.format(MessagesManager.getTextManager().getTextByKey("template.table.th", null), type.toString());
		}
		result += String.format(MessagesManager.getTextManager().getTextByKey("template.table.tr", null), tempString);
		
		// Table rows
		for (Map.Entry<String, Map<StatisticsTypes,Integer>> mapStringTypeInteger : _statistics.entrySet()) {
			tempString = String.format(MessagesManager.getTextManager().getTextByKey("template.table.td", null), 
					Constants.makeJSLink(mapStringTypeInteger.getKey(), 
							"handler:'%s', dest:'%s',%s:'%s',%s:'%s'",
							AdminMessageHandler._handler_name,
							AdminMessageHandler._defaultContentBodyID,
							AdminMessageHandler._what, AdminMessageHandler._what_hydra_bean_desc,
							AdminMessageHandler._kind, mapStringTypeInteger.getKey()));
			for (StatisticsTypes type : StatisticsTypes.values()) {
				tempString += String.format(MessagesManager.getTextManager().getTextByKey("template.table.td", null), mapStringTypeInteger.getValue().get(type));
			}
			result += String.format(MessagesManager.getTextManager().getTextByKey("template.table.tr", null), tempString);
		}
		
		return String.format(MessagesManager.getTextManager().getTextByKey("template.html.h4", null), 
					MessagesManager.getTextManager().getTextByKey("text.statistics.message.handled.by.objects", null))
				+ String.format(MessagesManager.getTextManager().getTextByKey("template.table.with.class", null), "statistics", result);
	}
}
