package org.hydra.messages;

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
public class CommonMessage implements IMessage {
	private Map<String, String> _requestDataMap = new HashMap<String, String>();
	private Map<String, String> _htmlContents = new HashMap<String, String>();
	private Set<String> _styleSheets = new HashSet<String>();
	private String _error;
	private HttpSession _session = null;

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
	public void setHttpSession(HttpSession inSession) {
		_session = inSession;
	}

	@Override
	public Result set2HttpSession(String inKey, Object inObj) {
		Result result = new Result();

		if (_session == null) {
			result.setResult(false);
			result.setResult("Invalid session!");
		} else {
			try {
				_session.setAttribute(inKey, inObj);
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

	@Override
	public void setRealPath(String path2File, String inDataKey) {
		getData().put(inDataKey,
				_session.getServletContext().getRealPath(path2File));
	}

	@Override
	public void setServerInfo(String inDataKey) {
		getData().put(inDataKey, _session.getServletContext().getServerInfo());
	}

/*	@Override
	public void bindHttpSessionWith(HttpSession httpSession) {
		httpSession = _session;
	}
*/	
}
