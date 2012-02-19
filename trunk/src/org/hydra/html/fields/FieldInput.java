package org.hydra.html.fields;

import org.hydra.utils.Utils;

public class FieldInput implements IField {
	private String ID = "";
	private String value = "";
	private int width = 25;
	private String  type = "text";
	private boolean readonly = false;

	public FieldInput(String inID) {
		setID(inID);  
	}	
	public FieldInput(String inID, String inValue) {
		setID(inID); setValue(inValue);  
	}
	
	public FieldInput(String inID, String inValue, boolean inReadOnly) {
		setID(inID); setValue(inValue); setReadonly(inReadOnly); 
	}
	
	
	public FieldInput(String inID, String inValue, String inType) {
		setID(inID); setValue(inValue);  setType(inType);
	}	
	
	public FieldInput(String inID, String inValue, int inWidth) {
		setID(inID); setValue(inValue); setWidth(inWidth); 
	}
	
	public FieldInput(String inID, String inValue, int inWidth, boolean inReadOnly) {
		setID(inID); setValue(inValue); setWidth(inWidth); setReadonly(inReadOnly); 
	}	
	
	public void setID(String iD) {
		ID = iD;
	}
	@Override	
	public String getID() {
		return ID;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}

	public void setWidth(int inWidth) {
		this.width = inWidth;
	}
	public int getWidth() {
		return width;
	}
	
	@Override
	public String getAsHtml(){
		if(isReadonly())
			return Utils.T(
					"template.html.custom.input.readonly.ID.Value.Wdth.Type", 
					getID(), 
					getValue(), 
					getWidth(), 
					getType());
		return Utils.T(
				"template.html.custom.input.ID.Value.Wdth.Type", 
				getID(), 
				getValue(), 
				getWidth(), 
				getType());
	}
	@Override	
	public String getValue4JS(){
		if(getType().compareToIgnoreCase("file") == 0)
			return String.format("dwr.util.getValue('%s')", getID());
		return String.format("jQuery('#%s').prop('value')", getID());
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isReadonly() {
		return readonly;
	}
}
