package org.hydra.utils.abstracts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ALogger {
	
	private Log _log = 	LogFactory.getLog(this.getClass());
	protected String trace = null;

	public void setLog(Log _log) {
		this._log = _log;
	}

	public Log getLog() {
		return _log;
	}	

}
