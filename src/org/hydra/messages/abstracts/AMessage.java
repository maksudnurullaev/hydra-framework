package org.hydra.messages.abstracts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Result;

/**
 * @author M.Nurullayev
 *
 */
public abstract class AMessage implements IMessage {	
	private Map<String, String> _data = new HashMap<String, String>();	
	private HttpSession _session = null;
	
	@Override
	public void setHttpSession(HttpSession inSession){
		_session = inSession;
	}
	
	@Override
	public Result setToHttpSession(String inKey, Object inObj){
		Result result = new Result();
		
		if(_session == null){
			result.setResult(false);
			result.setResult("Invalid session!");
		}else{
			try{
				_session.setAttribute(inKey, inObj);
				result.setResult(true);
			}catch (Exception e) {
				result.setResult(e.getMessage());
				result.setResult(false);
			}
		}
		return result;
	}
	
	@Override
	public void setData(Map<String, String> _data) {
		if(_data == null){
			return;
		}
		this._data = _data;
	}

	@Override
	public Map<String, String> getData() {
		return _data;
	}
	
	@Override
	public void setError(String inErrorMessage){
		getData().put(_data_what, _data_what_error_message);
		getData().put(_data_value, inErrorMessage);
	}
	
	@Override
    public void setHtmlContent(String inHtmlContent){
		getData().put(_data_what, _data_what_html_content);
		getData().put(_data_value, inHtmlContent);    	
    }

	@Override
	public void setRealPath(String path2File, String inDataKey) {
		getData().put(inDataKey, 
				_session.getServletContext().getRealPath(path2File));
	}

	@Override
	public void setServerInfo(String inDataKey){
		getData().put(inDataKey, _session.getServletContext().getServerInfo());
	}
	
	@Override
	public void setHttpSession2(HttpSession httpSession) {
		httpSession = _session;
	}	
}
