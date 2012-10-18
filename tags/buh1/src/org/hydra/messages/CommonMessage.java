package org.hydra.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.directwebremoting.io.FileTransfer;
import org.hydra.messages.interfaces.IMessage;

/**
 * @author M.Nurullayev
 * 
 */
public class CommonMessage implements IMessage {

	private static final long serialVersionUID = 1L;

	private String sessionId = null;

	private FileTransfer file = null;	
	private Map<String, String> data = new HashMap<String, String>();
	private HttpSession session = null;
	private String url = null;
	private Map<String, String> htmlContents = null;
	private List<String> highlightFields = null;
	private List<String> noHighlightFields = null;
	private boolean reloadPage = false;
	private String error = null;
	private String contextPath;
	private long timeOut;
		
	public CommonMessage(String inSessionId){
		this.sessionId = inSessionId;
	}
	public CommonMessage() {
	}
	@Override
	public void setSessionId(String inSessionId) {
		this.sessionId = inSessionId;
	}	
	
	@Override	
	public String getSessionId(){
		return(sessionId);
	}
	@Override
	public long getTimeout() {
		return(timeOut);
	}	
	@Override	
	public void setTimeout(long timeout){
		timeOut = timeout;
	}
	@Override
	public void setHtmlContents(String keyElementID, String htmlContent) {
		if (htmlContents == null)
			htmlContents = new HashMap<String, String>();
		htmlContents.put(keyElementID, htmlContent);
	}
	@Override
	public void setData(Map<String, String> _data) {
		this.data = _data;
	}

	@Override
	public Map<String, String> getData() {
		return data;
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
		if(getData().get("dest") != null &&  !getData().get("dest").isEmpty()){
			setHtmlContents(getData().get("dest"), inHtmlContent);
		} else {
			setError("Destination not defined!");
		}
	}

	@Override
	public Map<String, String> getHtmlContents() {
		return htmlContents;
	}

	@Override
	public void clearContent() {
		if (htmlContents != null)
			htmlContents.clear();
	}

	@Override
	public void setHighlightFields(List<String> _highlightFields) {
		this.highlightFields = _highlightFields;
	}

	@Override
	public List<String> getHighlightFields() {
		return highlightFields;
	}

	@Override
	public void setNoHighlightFields(String[] mandatoryFields) {
		this.noHighlightFields = new ArrayList<String>();
		for (String field : mandatoryFields)
			noHighlightFields.add(field);
	}

	@Override
	public List<String> getNoHighlightFields() {
		return noHighlightFields;
	}

	@Override
	public void setSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public HttpSession getSession() {
		return session;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setReloadPage(boolean reloadPage) {
		this.reloadPage = reloadPage;
	}

	@Override
	public boolean isReloadPage() {
		return reloadPage;
	}

	@Override
	public void setContextPath(String inContextPath) {
		contextPath = inContextPath;	
	}

	@Override
	public String getContextPath() {
		return(contextPath);	
	}

	@Override
	public void setFile(FileTransfer inFile) {
		file = inFile;		
	}

	@Override
	public FileTransfer getFile() {
		return(file);
	}

}
