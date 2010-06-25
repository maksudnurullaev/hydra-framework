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
	private Set<ColumnFamilyBean> cfLinks = null;
	private Set<ColumnFamilyBean> cfChilds = null;
	private String name = null;

	public void setColumns(Set<ColumnBean> inColumns) {
		for(ColumnBean column: inColumns){
			getLog().debug("Add column: " + getName());			
			this.columns.put(column.getName(), column);
		}
		getLog().debug(String.format("%s fields added to %s", inColumns.size(), getName()));
	}
	
	public void setChilds( Set<ColumnFamilyBean> inChilds){
		this.cfChilds = inChilds;
	}
	
	public void setLinks( Set<ColumnFamilyBean> inLinks){
		this.cfLinks = inLinks;
	}
	
	public boolean containsRelation(String inName){
		// links
		if(cfLinks != null)
			for(ColumnFamilyBean bean:cfChilds)
				if(bean.getName().equals(inName)) return true;
		
		// childs
		if(cfChilds != null)
			for(ColumnFamilyBean bean:cfChilds)
				if(bean.getName().equals(inName)) return true;
		
		return false;
	}	
	
	public Result getRelation(String inName){
		Result result = new Result();
		
		// childs
		if(cfChilds != null){
			for(ColumnFamilyBean bean:cfChilds)
				if(bean.getName().equals(name)){
					result.setResult(true);
					result.setObject(bean);
					return result;
				}
		}
		
		// links
		if(cfLinks != null){
			for(ColumnFamilyBean bean:cfLinks)
				if(bean.getName().equals(name)){
					result.setResult(true);
					result.setObject(bean);
					return result;
				}
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

	public Set<ColumnFamilyBean> getChilds() {
		return this.cfChilds;		
	}
}
