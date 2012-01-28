package org.hydra.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.WebApplication;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Result;

/**
 * @author M.Nurullayev
 * 
 */
public class CommonMessage implements IMessage {

	private static final long serialVersionUID = 1L;
	private WebApplication webApplication = null;

	private String locale = null;
	private String userId = null;
	
	private Map<String, String> requestDataMap = new HashMap<String, String>();
	private String sessionID = null;
	private String url = null;
	private Map<String, String> htmlContents = null ;
	private List<String> highlightFields = null;
	private List<String> noHighlightFields = null;
	private boolean reloadPage = false;
		
	private String error = null;
	private FileTransfer file = null;
	private WebContext webContext = null;

	@Override
	public void setWebContext(WebContext webContext) {
		this.webContext  = webContext;
	}	
	
	@Override
	public WebContext getWebContext() {
		return(webContext);
	}	
	
	@Override
	public void setHtmlContents(String keyElementID, String htmlContent) {
		if(htmlContents == null) htmlContents = new HashMap<String, String>();
		htmlContents.put(keyElementID, htmlContent);
	}

	@Override
	public Result set2HttpSession(String inKey, Object inObj) {
		Result result = new Result();
		
		WebContext context = getWebContext();		
		if (context == null || context.getSession() == null) {
			result.setResult(false); 
			result.setErrorString("Invalid session!");
		} else {
			try {
				context.getSession().setAttribute(webApplication.getId()	+ inKey, inObj);
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
		this.requestDataMap = _data;
	}

	@Override
	public Map<String, String> getData() {
		return requestDataMap;
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public void setError(String inErrorString) {
		clearContent();
		error = inErrorString;
	}

	@Override
	public void setHtmlContent(String inHtmlContent) {
		setHtmlContents(getData().get("dest"), inHtmlContent);
	}

	public Map<String, String> getHtmlContents() {
		return htmlContents;
	}
	
	public void clearContent() {
		if(htmlContents != null) htmlContents.clear();
	}

	public void setHighlightFields(List<String> _highlightFields) {
		this.highlightFields = _highlightFields;
	}

	public List<String> getHighlightFields() {
		return highlightFields;
	}

	public void setNoHighlightFields(String[] mandatoryFields) {
		this.noHighlightFields = new ArrayList<String>();
		for(String field:mandatoryFields)
			noHighlightFields.add(field);
	}

	public List<String> getNoHighlightFields() {
		return noHighlightFields;
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

	@Override
public boolean isReloadPage() {
		return reloadPage;
	}

	@Override
	public void setLocale(String inLocale) {
		this.locale = inLocale;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public void setUserId(String _user_id) {
		this.userId = _user_id;
	}

	@Override
	public String getIserId() {
		return userId;
	}
	
	@Override
	public WebApplication getWebApplication() {
		return webApplication;
	}

	@Override
	public void setWebApplication(WebApplication webApplication) {
		this.webApplication = webApplication;
	}
	
}
