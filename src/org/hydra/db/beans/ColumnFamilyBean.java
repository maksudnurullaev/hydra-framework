package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class ColumnFamilyBean extends ALogger {
	private Map<String, ColumnBean> columns = new HashMap<String, ColumnBean>();
	private String name = null;

	public void setColumnBeans(Set<ColumnBean> inColumns) {
		for(ColumnBean column: inColumns)
			this.columns.put(column.getName(), column);
		getLog().debug(String.format("%s fields added to Cf(%s)", inColumns.size(), getName()));
	}

	public Map<String, ColumnBean> getColumns() {
		return columns;
	}
	
	public ColumnBean getColumnByName(String inColumnName) {
		if(columns.containsKey(inColumnName))
			return columns.get(inColumnName);
		return null;
	}	

	public void setName(String name) {
		this.name = name;
		getLog().debug("Set Cf name to: " + getName());
	}

	public String getName() {
		return name;
	}
	
}
