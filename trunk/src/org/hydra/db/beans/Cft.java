package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;

import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class Cf extends ALogger {
	private Map<String, Key> keys = new HashMap<String, Key>();
	private String name = null;

	public void setKeys(Map<String, Key> inKeys) {
		this.keys = inKeys;
		getLog().debug(String.format("%s fields added to Cf(%s)", inKeys.size(), getName()));
	}

	public Map<String, Key> getKeys() {
		return keys;
	}

	public void setName(String name) {
		this.name = name;
		getLog().debug("Set Cf name to: " + getName());
	}

	public String getName() {
		return name;
	}
	
}
