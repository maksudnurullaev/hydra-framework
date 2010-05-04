package org.hydra.db.beans;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class Key extends ALogger {

	public static enum SUPER{COLUMNS, LINKS, UNKOWN};
	
	private SUPER _super = SUPER.UNKOWN;
	
	public void setSuper(SUPER inSuper) {
		this._super = inSuper;
		getLog().debug("Key super is: " + getSuper());
	}
	
	public void setSuper(String inSuper) {
		this._super = SUPER.valueOf(inSuper);
		getLog().debug("Key super is: " + getSuper());
	}

	public SUPER getSuper() {
		return _super;
	}
	
}
