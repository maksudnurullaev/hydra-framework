package org.hydra.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	//public String _session_id = null;
	public Moder _moder = new Moder(null);
	
	private Map<String, String> _requestDataMap = new HashMap<String, String>();
	private String sessionID = null;
	private Map<String, String> _htmlContents = new HashMap<String, String>();
	private String _styleSheet = null;
	private String _jsFile = null;
	private List<String> _highlightFields = new ArrayList<String>();
	private List<String> _noHighlightFields = new ArrayList<String>();
	
	private String _error = null;

	@Override
	public String getStyleSheet() {
		return _styleSheet;
	}

	@Override
	public void setStyleSheet(String styleSheet) {
		this._styleSheet = styleSheet;
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
		clearContent();
		_error = inErrorString;
	}

	@Override
	public void setHtmlContent(String inHtmlContent) {
		setHtmlContents(getData().get("dest"), inHtmlContent);
	}

	public Map<String, String> getHtmlContents() {
		return _htmlContents;
	}

	public void setJSFile(String inJSFilePath) {
		this._jsFile = inJSFilePath;
	}

	public String getJSFile() {
		return _jsFile;
	}

	public void clearContent() {
		_htmlContents.clear();
	}

	public void setHighlightFields(List<String> _highlightFields) {
		this._highlightFields = _highlightFields;
	}

	public List<String> getHighlightFields() {
		return _highlightFields;
	}

	public void setNoHighlightFields(String[] mandatoryFields) {
		this._noHighlightFields = new ArrayList<String>();
		for(String field:mandatoryFields)
			_noHighlightFields.add(field);
	}

	public List<String> getNoHighlightFields() {
		return _noHighlightFields;
	}
	
	@Override
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	@Override
	public String getSessionID() {
		return sessionID;
	}
}
