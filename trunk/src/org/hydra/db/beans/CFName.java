package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.abstracts.ALogger;

public class CFName extends ALogger {
	private Map<String, CFKey> fields = new HashMap<String, CFKey>();
	private String name = null;

	public void setFields(Map<String, CFKey> fields) {
		this.fields = fields;
		getLog().debug(String.format("%s fields added to CFName(%s)", fields.size(), getName()));
	}

	public Map<String, CFKey> getFields() {
		return fields;
	}

	public void setName(String name) {
		this.name = name;
		getLog().debug("Set CFName name to: " + getName());
	}

	public String getName() {
		return name;
	}
	
	public String getFieldsDescription(){
		String result = String.format("\tTable CFName(%s) statistics:\n", getName());
		for(Map.Entry<String, CFKey> entry: fields.entrySet()){
			result += String.format("\t\tCFKey(name,type) is (%s,%s).\n", entry.getKey(), entry.getValue().getType());
		}
		return result;
	}
}
