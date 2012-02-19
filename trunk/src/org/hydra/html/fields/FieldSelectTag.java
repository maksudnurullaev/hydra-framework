package org.hydra.html.fields;

import java.util.List;

import org.hydra.utils.Utils;

public class FieldSelectTag implements IField {
	private String ID = "";
	private String value = "";
	private List<String> tagPrefixes = null;
	private String appId = "";

	public FieldSelectTag(String inAppId, String inID, String inValue, List<String> inTagPrefix) {
		appId = inAppId;
		ID = inID;
		value = inValue;
		tagPrefixes = inTagPrefix; 
	}

	@Override
	public String getAsHtml() {
		return Utils.tagsAsEditableHtml(appId, ID, value, null, null, tagPrefixes);
	}

	@Override
	public String getValue4JS() {
		return String.format("jQuery('#%s').prop('value')", getID());
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
