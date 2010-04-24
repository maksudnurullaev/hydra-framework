package org.hydra.messages.abstracts;

import java.util.HashMap;
import java.util.Map;

import org.hydra.messages.interfaces.IMessage;

/**
 * @author M.Nurullayev
 *
 */
public abstract class AMessage implements IMessage {

	
	private Map<String, String> _data = null;	
	
			
	@Override
	public void setData(Map<String, String> _data) {
		this._data = _data;
	}

	@Override
	public Map<String, String> getData() {
		return _data;
	}
	
	@Override
	public void setError(String inErrorMessage){
		if(_data == null) setData(new HashMap<String, String>());
		getData().put(_data_what, _data_what_error_message);
		getData().put(_data_value, inErrorMessage);
	}
	
	@Override
    public void setHtmlContent(String inHtmlContent){
    	if(_data == null) setData(new HashMap<String, String>());
		getData().put(_data_what, _data_what_html_content);
		getData().put(_data_value, inHtmlContent);    	
    }
}
