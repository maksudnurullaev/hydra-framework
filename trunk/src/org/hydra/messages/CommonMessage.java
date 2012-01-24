package org.hydra.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.WebContext;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.WebApplication;
import org.hydra.messages.interfaces.IMessage;
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
	
	private Map<String, String> _requestDataMap = new HashMap<String, String>();
	private String sessionID = null;
	private String url = null;
	private Map<String, String> _htmlContents = null ;
	private List<String> _highlightFields = null;
	private List<String> _noHighlightFields = null;
	private boolean reloadPage = false;
		
	private String _error = null;
	private FileTransfer file = null;

	@Override
	public void setHtmlContents(String keyElementID, String htmlContent) {
		if(_htmlContents == null) _htmlContents = new HashMap<String, String>();
		_htmlContents.put(keyElementID, htmlContent);
	}

	@Override
	public Result set2HttpSession(String inKey, Object inObj) {
		Result result = new Result();

		if (_web_context == null || _web_context.getSession() == null) {
			result.setResult(false);
			result.setErrorString("Invalid session!");
		} else {
			try {
				_web_context.getSession().setAttribute(_web_application.getId()	+ inKey, inObj);
				result.setResult(true);
			} catch (Exception e) {
				result.setErrorString(e.getMessage());
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
	
	public void clearContent() {
		if(_htmlContents != null) _htmlContents.clear();
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
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String getUrl() {
		return url;
	}
	@Override
	public void setFile(FileTransfer file) {
		this.file = file;
	}
	@Override
	public FileTransfer getFile() {
		return file;
	}
	public void setReloadPage(boolean reloadPage) {
		this.reloadPage = reloadPage;
	}

	public boolean isReloadPage() {
		return reloadPage;
	}
}
