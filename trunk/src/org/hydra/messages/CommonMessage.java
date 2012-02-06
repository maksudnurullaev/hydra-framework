package org.hydra.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;
import org.hydra.messages.interfaces.IMessage;

/**
 * @author M.Nurullayev
 * 
 */
public class CommonMessage implements IMessage {

	private static final long serialVersionUID = 1L;

	public String fileRealPath = null;
	public String filePath = null;
	public FileTransfer file = null;

	private String userId = null;
	private Map<String, String> data = new HashMap<String, String>();
	private String sessionID = null;
	private String url = null;
	private Map<String, String> htmlContents = null;
	private List<String> highlightFields = null;
	private List<String> noHighlightFields = null;
	private boolean reloadPage = false;
	private String error = null;
	private String contextPath;
	private long timeOut;

	public long getTimeout() {
		return(timeOut);
	}	
	
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
		setHtmlContents(getData().get("dest"), inHtmlContent);
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

	public void setReloadPage(boolean reloadPage) {
		this.reloadPage = reloadPage;
	}

	@Override
	public boolean isReloadPage() {
		return reloadPage;
	}

	@Override
	public void setUserId(String _user_id) {
		this.userId = _user_id;
	}

	@Override
	public String getUserId() {
		return userId;
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
	public void setFileRealPath(String inFileRealPath) {
		fileRealPath = inFileRealPath;
	}

	@Override
	public FileTransfer getFile() {
		return (file);
	}

	@Override
	public void setFilePath(String inUri4File) {
		filePath = inUri4File;
	}

	@Override
	public String getFilePath() {
		return(filePath);
	}
	
	@Override
	public String getFileRealPath() {
		return(fileRealPath);
	}
}
