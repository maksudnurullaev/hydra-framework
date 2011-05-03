package org.hydra.messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.directwebremoting.WebContext;
import org.hydra.beans.WebApplication;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Moder;
import org.hydra.utils.Result;

/**
 * @author M.Nurullayev
 * 
 */
public class CommonMessage implements IMessage {
	public WebApplication _web_application = null;
	public WebContext _web_context = null;
	public String _locale = null;
	public String _user_id = null;
	public String _session_id = null;
	
	private Map<String, String> _requestDataMap = new HashMap<String, String>();
	private Map<String, String> _htmlContents = new HashMap<String, String>();
	private Set<String> _styleSheets = new HashSet<String>();
	private String _error = null;
	public Moder _moder = new Moder(null);

	@Override
	public Set<String> getStyleSheets() {
		return _styleSheets;
	}

	@Override
	public void setStyleSheets(Set<String> styleSheets) {
		this._styleSheets = styleSheets;
	}

	@Override
	public void setHtmlContents(String keyElementID, String htmlContent) {
		_htmlContents.put(keyElementID, htmlContent);
	}

	@Override
	public Result set2HttpSession(String inKey, Object inObj) {
		Result result = new Result();

		if (_web_context == null || _web_context.getSession() == null) {
			result.setResult(false);
			result.setResult("Invalid session!");
		} else {
			try {
				_web_context.getSession().setAttribute(_web_application.getId()	+ inKey, inObj);
				result.setResult(true);
			} catch (Exception e) {
				result.setResult(e.getMessage());
				result.setResult(false);
			}
		}
		return result;
	}

	@Override
	public void setData(Map<String, String> _data) {
		this._requestDataMap = _data;
	}

	@Override
	public Map<String, String> getData() {
		return _requestDataMap;
	}

	@Override
	public String getError() {
		return _error;
	}

	@Override
	public void setError(String inErrorString) {
		_error = inErrorString;
	}

	@Override
	public void setHtmlContent(String inHtmlContent) {
		setHtmlContents(getData().get("dest"), inHtmlContent);
	}

	public Map<String, String> getHtmlContents() {
		return _htmlContents;
	}
}
