package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.db.beans.ColumnBean.COLUMN_TYPES;
import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class ColumnFamilyBean extends ALogger {
	private Map<String, ColumnBean> columns = new HashMap<String, ColumnBean>();
	private Map<String, ColumnBean> links = new HashMap<String, ColumnBean>();
	private String name = null;

	public void setColumnBeans(Set<ColumnBean> inColumns) {
		for(ColumnBean column: inColumns){
			getLog().debug(String.format("Add column/type: %s/%s to %s",
					column.getName(),
					column.getTType(),
					getName()));
			if(column.getTType() == COLUMN_TYPES.COLUMNS)
				this.columns.put(column.getName(), column);
			else if(column.getTType() == COLUMN_TYPES.LINKS)
				this.links.put(column.getName(), column);
			else
				getLog().error("Unkown column type: " + column.getTType());
		}
		getLog().debug(String.format("%s fields added to %s", inColumns.size(), getName()));
	}

	public Map<String, ColumnBean> getColumns() {
		return columns;
	}
	
	public Map<String, ColumnBean> getLinks() {
		return links;
	}		

	public void setName(String name) {
		this.name = name;
		getLog().debug("Setup CF name: " + getName());
	}

	public String getName() {
		return name;
	}

	public ColumnBean getAnyColumnOrLinkByName(String columnName) {
		if(columns != null && columns.containsKey(columnName)) return columns.get(columnName);
		if(links != null && links.containsKey(columnName)) return links.get(columnName);

		getLog().error("Could not find column or link description for name: " + columnName);
		return null;
	}
}