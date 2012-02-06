package org.hydra.messages.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

public interface IMessage extends Serializable {	
	Map<String, String> getData();
	String getError();
	void setData(Map<String, String> inData);
	void setError(String inErrorMessage);
	void setHtmlContent(String inHtmlContent);
	void setHtmlContents(String inKeyElementID, String inHtmlContent);
	void setSessionID(String inSessionID);
	String getSessionID();
	void setUrl(String inUrl);
	String getUrl();
	boolean isReloadPage();
	void setUserId(String inUserId);
	String getUserId();
	void setContextPath(String inContextPath);
	String getContextPath();
	void setTimeout(long timeout);
	void setHighlightFields(List<String> errorFields);
	long getTimeout();
	void setReloadPage(boolean b);
	void clearContent();
	void setNoHighlightFields(String[] mandatoryFields);
	void setFile(FileTransfer inFile);
	void setFileRealPath(String realPath);
	FileTransfer getFile();
	void setFilePath(String uri4File);
	String getFileRealPath();
	List<String> getNoHighlightFields();
	List<String> getHighlightFields();
	Map<String, String> getHtmlContents();
	String getFilePath();
}