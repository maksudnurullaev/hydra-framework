package org.hydra.messages.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

public interface IMessage extends Serializable {	
	void clearContent();
	String getContextPath();
	Map<String, String> getData();
	String getError();
	FileTransfer getFile();
	List<String> getHighlightFields();
	Map<String, String> getHtmlContents();
	List<String> getNoHighlightFields();
	String getSessionID();
	long getTimeout();
	String getUrl();
	String getUserId();
	boolean isReloadPage();
	void setContextPath(String inContextPath);
	void setData(Map<String, String> inData);
	void setError(String inErrorMessage);
	void setFile(FileTransfer inFile);
	void setHighlightFields(List<String> errorFields);
	void setHtmlContent(String inHtmlContent);
	void setHtmlContents(String inKeyElementID, String inHtmlContent);
	void setNoHighlightFields(String[] mandatoryFields);
	void setReloadPage(boolean b);
	void setSessionID(String inSessionID);
	void setTimeout(long timeout);
	void setUrl(String inUrl);
	void setUserId(String inUserId);
}