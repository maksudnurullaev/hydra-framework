package org.hydra.utils;

public class FieldInput implements IField {
	private String ID = "";
	private String value = "";
	private int minWidth = 25;
	private int maxWidth = 25;
	
	public FieldInput(String inID, String inValue) {
		setID(inID); setValue(inValue);  
	}
	public FieldInput(String inID, String inValue, int inMinWidth, int inMaxWidth) {
		setID(inID); setValue(inValue); setMinWidth(inMinWidth); setMaxWidth(inMaxWidth); 
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
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}
	public int getMinWidth() {
		return minWidth;
	}
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	public int getMaxWidth() {
		return maxWidth;
	}
	
	@Override
	public String getAsHtml(){
		return Utils.T(
				"template.html.custom.input.ID.Value.MaxWdth.Wdth", 
				getID(), 
				getValue(), 
				getMaxWidth(), 
				getMinWidth());
	}
	@Override	
	public String getValue4JS(){
		return String.format("$('%s').value", getID());
	}
}
