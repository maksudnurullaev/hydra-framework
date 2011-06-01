package org.hydra.html.fields;

import org.hydra.utils.Utils;

public class FieldSelectTag implements IField {
	private String ID = "";
	private String value = "";
	private String tagPrefix = "";
	private String appId = "";

	public FieldSelectTag(String inAppId, String inID, String inValue, String inTagPrefix) {
		appId = inAppId;
		ID = inID;
		value = inValue;
		tagPrefix = inTagPrefix; 
	}

	@Override
	public String getAsHtml() {
		StringBuffer ss = new StringBuffer();
		ss.append(String.format("<select id=\"%s\" style=\"border: 1px solid rgb(127, 157, 185);\">", getID()));
		for(String tag:Utils.getAllTags4(appId, tagPrefix, tagPrefix)){
			if(getValue().compareTo(tag) == 0)// selected
				ss.append(String.format("<option value=\"%s\" selected>%s</option>", tag, tag));
			else
				ss.append(String.format("<option value=\"%s\">%s</option>", tag, tag));				
		}
		ss.append("</select>");
		return ss.toString();
	}

	@Override
	public String getValue4JS() {
		return String.format("$('%s').value", getID());
	}

	@Override
	public String getID() {
		return ID;
	}

	public void setID(String inID) {
		ID = inID;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
