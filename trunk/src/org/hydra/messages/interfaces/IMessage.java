package org.hydra.messages.interfaces;

import java.io.Serializable;
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
	void setFile(FileTransfer inFile);
	FileTransfer getFile();
	boolean isReloadPage();
	void setUserId(String inUserId);
	String getUserId();
	void setRealFilePath(String inRealFilePath);
	String getRealFilePath();
	void setContextPath(String inContextPath);
	String getContextPath();
}