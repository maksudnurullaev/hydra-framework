package org.hydra.messages.interfaces;

import java.io.Serializable;
import java.util.Map;

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
}