package org.hydra.db.beans;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class ColumnBean extends ALogger {

	public static final String SUPER_COLUMN = "COLUMN";
	public static final String SUPER_LINK = "LINK";
	
	private String name = null;
	
	private String _super = null;
		
	public void setSuper(String inSuper) {
		if(inSuper != SUPER_COLUMN || inSuper != SUPER_LINK){
			getLog().fatal("Could not find proper super property name for column!");
			_super = null;
		}
		this._super = inSuper;
		getLog().debug("Key super is: " + getSuper());
	}
	
	public String getSuper() {
		return _super;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public boolean isColumn(){
		return _super != null && SUPER_COLUMN.equals(_super);
	}
	
	public boolean isLink(){
		return _super != null && SUPER_LINK.equals(_super);
	}
}
