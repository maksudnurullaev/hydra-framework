package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.deployers.Deployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.ERROR_CODES;
import org.hydra.utils.StringWrapper;
import org.hydra.utils.Utils;

public class DBRequest extends AMessageHandler{ // NO_UCD

	private void getTextarea2Edit(
			CommonMessage inMessage,
			String inCFName,
			String inCName, 
			String updateHandlerName,
			String updateHandlerMethodName){
		
		String key = inMessage.getData().get("key");
		StringWrapper stringWrapper = new StringWrapper(); 
		
		ERROR_CODES err = DBUtils.getValue(inMessage._web_application.getId(), inCFName, key, inCName, stringWrapper);
		if(err != ERROR_CODES.NO_ERROR && err != ERROR_CODES.ERROR_NO_VALUE){
			getLog().error(err.toString());
			inMessage.setError(err.toString());
			return;
		}
				
		StringBuffer resultBuffer = new StringBuffer("<div id=\"editBox\" class=\"editbox\">");
		
		resultBuffer.append("<sup class='editorlinks' id='").append(key).append(".editorlinks").append("'>");
		resultBuffer.append("&nbsp;<a onclick=\"javascript:void(Globals.uploadIt('").append(key).append("','").append(updateHandlerName).append("','").append(updateHandlerMethodName).append("')); return false;\" href=\"#\">Upload</a>");
		resultBuffer.append(" | <a onclick=\"javascript:void(Globals.hideEditBox()); return false;\" href=\"#\">Close</a>");
		
		resultBuffer.append(" </sup><br/><sup>");
		resultBuffer.append("&nbsp;<strong>");
		resultBuffer.append(key);
		resultBuffer.append("  </strong>");		
		resultBuffer.append(" </sup><br/>");
		
		resultBuffer.append("<textarea class='edittextarea' id='").append(key).append(".textarea'>");
		resultBuffer.append(err == ERROR_CODES.NO_ERROR?stringWrapper.getString():err.toString());
		resultBuffer.append("</textarea>");
		
		resultBuffer.append("</div>");
		
		inMessage.setHtmlContent(resultBuffer.toString());
	}	
	
	public IMessage editText(CommonMessage inMessage){
		if(!testData(	inMessage, "key", "dest")) return inMessage;
		
		getTextarea2Edit(inMessage, "Text", inMessage._locale,
				"DBRequest","updateText");
		return inMessage;
	}	
	
	public IMessage editTemplate(CommonMessage inMessage){
		if(!testData(	inMessage, "key", "dest")) return inMessage;
		
		getTextarea2Edit(inMessage, "Template", "html",
				"DBRequest", "updateTemplate");
		return inMessage;
	}	

	
	public IMessage updateText(CommonMessage inMessage){
		if(!testData(	inMessage, "value", "dest")) return inMessage;
		
		String key = inMessage.getData().get("dest");
		String value = inMessage.getData().get("value");		
				
		update(inMessage, "Text", key, inMessage._locale, value);
		
		return inMessage;
	}
	
	private void update(
			CommonMessage inMessage, 
			String inCFName, 
			String inKey, 
			String inColumnName, 
			String inValue){
		
		ERROR_CODES err = DBUtils.setValue(inMessage._web_application.getId(), inCFName, inKey, inColumnName, inValue);
		
		if(err != ERROR_CODES.NO_ERROR){
			getLog().error(err.toString());
			inMessage.setError(err.toString());
			return ;
		}		
		
		StringWrapper outValue = new StringWrapper();
		err = DBUtils.getValue(inMessage._web_application.getId(), inCFName, inKey, inColumnName, outValue);
		
		if(err != ERROR_CODES.NO_ERROR){
			getLog().error(err.toString());
			inMessage.setError(err.toString());
			return;
		}
		
		List<String> links = new ArrayList<String>();
		String htmlContent = Deployer.deployContent(outValue.getString(), inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
	}
	
	public IMessage updateTemplate(CommonMessage inMessage){
		if(!testData(	inMessage, "value", "dest")) return inMessage;
		
		String key = inMessage.getData().get("dest");
		String value = inMessage.getData().get("value");		
		
		update(inMessage, "Template", key, "html", value);
		
		return inMessage;
	}	
}
