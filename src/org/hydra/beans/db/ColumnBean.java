package org.hydra.beans.db;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class ColumnBean extends ALogger {

	private String _name = null;
	
	public void setName(String name) {
		this._name = name;
		
		if(name == null){
			getLog().warn("Column name is NULL now!");
			return;
		}
		getLog().debug("Set column name: " + this._name);
	}

	public String getName() {
		return _name;
	}
	
}
