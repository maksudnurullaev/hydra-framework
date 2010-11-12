package org.hydra.beans.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class KeyspaceBean extends ALogger {
	private Map<String, ColumnFamilyBean> _columnFamilies = new HashMap<String, ColumnFamilyBean>();
	
	private String name = null;
	private String linkTableName = null;

	public void setColumnFamilies(Set<ColumnFamilyBean> inColumnFamilies) {
		_columnFamilies.clear();
		for(ColumnFamilyBean entryCF: inColumnFamilies)
			_columnFamilies.put(entryCF.getName(), entryCF);
		
		getLog().debug(String.format("ColumnFamily(%s) added to Keyspace(%s)", _columnFamilies.size(), getName()));		
	}
	
	public Map<String, ColumnFamilyBean> getColFamilies(){
		return _columnFamilies;
	}
	
	public void setName(String name) {
		this.name = name;
		getLog().debug("Set keyspace name to: " + getName());
	}
	
	public String getName() {
		return name;
	}
	
	public ColumnFamilyBean getColumnFamilyByName(String inCfName){
		if(_columnFamilies.containsKey(inCfName)){
			getLog().debug(String.format("Found column family(%s) in keyspace(%s)",
					inCfName,
					getName()));
			return _columnFamilies.get(inCfName);
		}
		getLog().warn(String.format("Could not find column family(%s) in keyspace(%s)",
				inCfName,
				getName()));
		return null;
	}
	
	public void setLinkTableName(String linkTableName) {
		this.linkTableName = linkTableName;
	}
	
	public String getLinkTableName() {
		return linkTableName;
	}

	public boolean containsColumnFamily(String inColumnFamilyName) {
		if(_columnFamilies != null)
			return _columnFamilies.containsKey(inColumnFamilyName);
		return false;
	}

	
	
}
