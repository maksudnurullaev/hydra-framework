package org.hydra.messages.interfaces;

import java.util.Map;

import org.directwebremoting.io.FileTransfer;
import org.hydra.utils.Result;

public interface IMessage {	
	Map<String, String> getData();
	String getError();
	String getStyleSheet();
	Result set2HttpSession(String inKey, Object inObj);
	void setData(Map<String, String> data);
	void setError(String inErrorMessage);
	void setHtmlContent(String inHtmlContent);
	void setHtmlContents(String keyElementID, String htmlContent);
	void setSessionID(String sessionID);
	String getSessionID();
	void setStyleSheet(String styleSheet);
	void setUrl(String url);
	String getUrl();
	void setFile(FileTransfer file);
	FileTransfer getFile();
}