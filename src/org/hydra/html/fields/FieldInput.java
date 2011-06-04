package org.hydra.html.fields;

import org.hydra.utils.Utils;

public class FieldInput implements IField {
	private String ID = "";
	private String value = "";
	private int width = 25;
	private String  type = "text";
	
	public FieldInput(String inID, String inValue) {
		setID(inID); setValue(inValue);  
	}
	
	public FieldInput(String inID, String inValue, String inType) {
		setID(inID); setValue(inValue);  setType(inType);
	}	
	
	public FieldInput(String inID, String inValue, int inWidth) {
		setID(inID); setValue(inValue); setWidth(inWidth); 
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
		return Utils.T(
				"template.html.custom.input.ID.Value.Wdth.Type", 
				getID(), 
				getValue(), 
				getWidth(), 
				getType());
	}
	@Override	
	public String getValue4JS(){
		return String.format("$('%s').value", getID());
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
}
