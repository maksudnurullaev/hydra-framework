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
	private String linkTableName = null;

	public void setColumnFamilies(Set<ColumnFamilyBean> inColumnFamilies) {
		_columnFamilies.clear();
		for(ColumnFamilyBean entryCF: inColumnFamilies){
			getLog().debug("Add CF: " + entryCF.getName());
			_columnFamilies.put(entryCF.getName(), entryCF);
		}
	}
	
	public Map<String, ColumnFamilyBean> getColFamilies(){
		return _columnFamilies;
	}
	
	public ColumnFamilyBean getColumnFamilyByName(String inCfName){
		if(_columnFamilies.containsKey(inCfName)){
			getLog().debug("Found CF: " + inCfName); 
			return _columnFamilies.get(inCfName);
		}
		getLog().warn("Could not find CF: " + inCfName);
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

	public String getName(){ return null;} //TODO Remove it later
	
	
}
