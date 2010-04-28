package org.hydra.db.beans;

import org.hydra.utils.abstracts.ALogger;

public class CFKey extends ALogger {

	public static enum TYPE{COLUMNS, LINKS};
	
	private TYPE type;
	
	public void setType(TYPE type) {
		this.type = type;
		getLog().debug("Field type is: " + getType());
	}
	
	public void setType(String inType) {
		this.type = TYPE.valueOf(inType);
		getLog().debug("Field type is: " + getType());
	}

	public TYPE getType() {
		return type;
	}
	
}
