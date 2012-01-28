package org.hydra.messages.interfaces;

import java.io.Serializable;
import java.util.Map;

import org.directwebremoting.WebContext;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.WebApplication;
import org.hydra.utils.Result;

public interface IMessage extends Serializable {	
	Map<String, String> getData();
	String getError();
	Result set2HttpSession(String inKey, Object inObj);
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
	WebContext getWebContext();
	void setWebContext(WebContext inWebContext);
	boolean isReloadPage();
	void setLocale(String inLocale);
	String getLocale();
	void setUserId(String inUserId);
	String getIserId();
	WebApplication getWebApplication();
	void setWebApplication(WebApplication inWebApplication);
}