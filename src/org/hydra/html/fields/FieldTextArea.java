package org.hydra.html.fields;

import org.hydra.utils.Utils;

public class FieldTextArea extends FieldInput {
	private String tags = "";
	public FieldTextArea(String inID, String inValue, String inTags) {
		super(inID, inValue);
		setTags(inTags);
	}
	public FieldTextArea(String inID, String inValue, int inMinWidth, int inMaxWidth) {
		super(inID,inValue,inMinWidth,inMaxWidth); 
	}
	
	/* (non-Javadoc)
	 * @see org.hydra.html.fields.FieldInput#getAsHtml()
	 */
	@Override
	public String getAsHtml() {
		return Utils.T(
				"template.html.custom.textarea.ID.Tags.Value", 
				getID(), 
				getTags(),
				getValue());
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getTags() {
		return tags;
	}

}
