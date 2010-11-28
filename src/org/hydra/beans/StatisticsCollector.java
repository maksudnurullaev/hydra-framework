package org.hydra.beans;

import java.util.HashMap;
import java.util.Map;

import org.hydra.beans.interfaces.IStatisticsCollector;
import org.hydra.messages.interfaces.IMessage;
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
		if(inStatType == StatisticsTypes.ACCEPTED)
			getLog().debug(String.format("Fixed %s statistics type for: %s", inStatType.toString(), inObjectName));
		else
			getLog().warn(String.format("Fixed %s statistics type for: %s", inStatType.toString(), inObjectName));
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
				totals.put(type, totals.get(type) + mapStringTypeInteger.getValue().get(type));
			}
			result.append("\n");
		}
		
		return result.toString();
	}

	public int getMessagesTotal4(String inObjectName) {
		if(!_statistics.containsKey(inObjectName)) return -1;
		
		int result = 0;
		
		for (StatisticsTypes type : StatisticsTypes.values()) {
			result += _statistics.get(inObjectName).get(type);
		}
		return result;
	}

	public String getHtmlReport(IMessage inMessage) {
		String result = ""; 
		String tempString = "";		
		// Table header
		tempString += String.format("<td>%s</td>", "&nbsp;");
		for (StatisticsTypes type : StatisticsTypes.values()) {
			tempString += String.format("<td>&nbsp;&nbsp;&nbsp;<u>%s</u></td>", type.toString());
		}
		result += String.format(MessagesManager.getTemplate("template.table.tr"), tempString);
		// Table rows
		for (Map.Entry<String, Map<StatisticsTypes,Integer>> mapStringTypeInteger : _statistics.entrySet()) {
			tempString = String.format("<td><u>%s</u>:</td>",mapStringTypeInteger.getKey());			
			for (StatisticsTypes type : StatisticsTypes.values()) {
				tempString += String.format("<td>%s</td>", 
						mapStringTypeInteger.getValue().get(type));
			}
			result += String.format("<tr class='tr'>%s</tr>", tempString);
		}
		
		return String.format(MessagesManager.getTemplate("template.table.with.class"), "statistics", result);
	}
}
