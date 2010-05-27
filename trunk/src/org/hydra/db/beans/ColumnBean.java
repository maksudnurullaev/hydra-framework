package org.hydra.db.beans;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class ColumnBean extends ALogger {

	public enum COLUMN_TYPES {
		UNDEFINED, COLUMNS, LINKS
	};
	
	private String _name = null;
	
	private COLUMN_TYPES _type = COLUMN_TYPES.UNDEFINED;
		
	public void setType(String inType) {
		try{
			_type = COLUMN_TYPES.valueOf(inType);
		}catch(Exception e){
			getLog().error("Could not setup column bean's type to: " + inType);
			_type = COLUMN_TYPES.UNDEFINED;
		}
	}
	
	public COLUMN_TYPES getTType() {
		return _type;
	}

	public String getType(){
		return _type.toString();
	}
	
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
