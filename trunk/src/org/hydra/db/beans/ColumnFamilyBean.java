package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.utils.Result;
import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class ColumnFamilyBean extends ALogger {
	public Map<String, ColumnBean> columns = new HashMap<String, ColumnBean>();
	private Set<ColumnFamilyBean> links = null;
	private String name = null;

	public void setColumns(Set<ColumnBean> inColumns) {
		for(ColumnBean column: inColumns){
			getLog().debug("Add column: " + getName());			
			this.columns.put(column.getName(), column);
		}
		getLog().debug(String.format("%s fields added to %s", inColumns.size(), getName()));
	}
	
	public void setLinks( Set<ColumnFamilyBean> inLinks){
		this.links = inLinks;
	}
	
	public Set<ColumnFamilyBean> getLinks() {
		return links;
	}		
	
	public boolean containsLink(String inName){
		if(links == null) return false;
		
		for(ColumnFamilyBean bean:links)
			if(bean.getName().equals(inName)) return true;
		
		return false;
	}	
	
	public Result getLink(String inName){
		Result result = new Result();
		if(links == null){
			result.setResult(false);
			result.setResult("Links is NULL!");
		}
		
		for(ColumnFamilyBean bean:links)
			if(bean.getName().equals(name)){
				result.setResult(true);
				result.setObject(bean);
				return result;
			}
		
		result.setResult(false);
		result.setResult("Link not found!");
		return result;
	}

	public void setName(String inName) {
		this.name = inName;
		getLog().debug("Setup CF name: " + getName());
	}

	public String getName() {
		return name;
	}

	public Result getColumn(String inName) {
		Result result = new Result();
		
		if(!columns.containsKey(inName)){
			result.setResult(false);
			result.setResult("Column not found!");
			return result;
		}
		
		result.setResult(true);
		result.setObject(columns.get(inName));
		return result;
	}

	public boolean containsColumn(String columnName) {
		return columns.containsKey(columnName);
	}
}
