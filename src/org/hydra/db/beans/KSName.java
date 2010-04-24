package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;

import org.hydra.utils.abstracts.ALogger;

public class KSName extends ALogger {
	private Map<String, CFName> tables = new HashMap<String, CFName>();
	private String name = null;
	private String linkTableName = null;
	
	public void setTables(Map<String, CFName> tables) {
		this.tables = tables;
		getLog().debug(String.format("%s tables added to KSName(%s)", tables.size(), getName()));		
	}
	public Map<String, CFName> getTables() {
		return tables;
	}
	
	public void setName(String name) {
		this.name = name;
		getLog().debug("Set KSName name to: " + getName());
	}
	
	public String getName() {
		return name;
	}
	
	public String getTablesDescriptionHTML() {
		String formatStrong = "%s<strong>%s:</strong> %s<br />";
		// KSName
		StringBuffer result = new StringBuffer(String.format(formatStrong, "", "KSName", getName()));		
		for(Map.Entry<String, CFName> entryKeyCFName: tables.entrySet()){
			// CFName
			result.append(String.format(formatStrong,"&nbsp;", "CFName",entryKeyCFName.getKey()));
			// CName
			for(Map.Entry<String, CFKey> entryKeyCFKey: entryKeyCFName.getValue().getFields().entrySet()){
				result.append(String.format(formatStrong,"&nbsp;&nbsp;", "CFKey", 
						String.format("%s, %s", 
								entryKeyCFKey.getKey(), 
								entryKeyCFKey.getValue().getType()), 
						""));				
			}
		}
		return result.toString();		
	}
	
	public String getTablesDescriptionText(){
		String result = String.format("KSName(%s) statistics:\n", getName());
		result += String.format("\tLink table is CFName(%s).\n", getLinkTableName());
		for(Map.Entry<String, CFName> entry: tables.entrySet()){
			result += entry.getValue().getFieldsDescription();
		}
		return result;		
	}
	
	
	public void setLinkTableName(String linkTableName) {
		this.linkTableName = linkTableName;
	}
	public String getLinkTableName() {
		return linkTableName;
	}
	
	
}
