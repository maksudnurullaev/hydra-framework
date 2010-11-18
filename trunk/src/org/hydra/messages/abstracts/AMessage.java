package org.hydra.messages.abstracts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Result;

/**
 * @author M.Nurullayev
 *
 */
public abstract class AMessage implements IMessage {	
	private Map<String, String> _internalData = new HashMap<String, String>();
	private Map<String, String> _htmlContents2 = new HashMap<String, String>();
	
	private Set<String> _styleSheets = new HashSet<String>();
	public Set<String> getStyleSheets() {
		return _styleSheets;
	}

	public void setStyleSheets(Set<String> styleSheets) {
		this._styleSheets = styleSheets;
	}

	private String _error;
	private HttpSession _session = null;
	
	public void setHtmlContents(String keyElementID, String htmlContent){
		_htmlContents2.put(keyElementID, htmlContent);
	}
	
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
		this._internalData = _data;
	}

	@Override
	public Map<String, String> getData() {
		return _internalData;
	}

	public String getError(){
		return _error;
	}
	@Override
	public void setError(String inErrorString){
		_error = inErrorString;
	}
	
	@Override
    public void setHtmlContent(String inHtmlContent){
		setHtmlContents(getData().get("dest"), inHtmlContent);
    }
	
	public Map<String, String> getHtmlContents(){
		return _htmlContents2;
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
