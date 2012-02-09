package org.hydra.messages.handlers;

import org.hydra.deployers.ADeployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.ErrorUtils.ERROR_CODES;
import org.hydra.utils.StringWrapper;
import org.hydra.utils.Utils;

public class DBRequest extends AMessageHandler{ // NO_UCD

	private void getTextarea2Edit(
			CommonMessage inMessage,
			String inCFName,
			String inKey,
			String inCName, 
			String inAction,
			String inActionMethod){
		
		String appId = inMessage.getData().get("appid");
		String spanId = String.format("%s.%s", inCFName, inKey); 
		String textAreaId =  String.format("%s.%s.%s.textarea", appId, inCFName, inKey);
		
		String jsData = Utils.jsData(
					"appid", Utils.Q(appId)
					, "handler", Utils.Q(inAction)
					, "action", Utils.Q(inActionMethod)
					, "key", Utils.Q(inKey)
					, "value", String.format("$('%s').value",textAreaId)
					, "dest", Utils.Q(spanId)
				);
		
		StringWrapper stringWrapper = new StringWrapper();		
		ErrorUtils.ERROR_CODES err = DBUtils.getValue(inMessage.getData().get("appid"), inCFName, inKey, inCName, stringWrapper);
		
		
		//TODO Impotant - replace </textarea> from source if template exist
		if( err == ERROR_CODES.NO_ERROR){
			String str = stringWrapper.getString();
			str = str.replaceAll("(?i)</textarea>", "[[Dictionary|Template|template.textarea.endtag|html]]");
			stringWrapper.setString(str);
		}		
		
		StringBuffer resultBuffer = new StringBuffer("<div class=\"edit\">");
		
		resultBuffer.append(Utils.T("template.html.a.onClick.sendMessage.Label"
				, jsData
				, "Upload"));	
		resultBuffer.append(" | <a onclick=\"javascript:void(Globals.hideEditBox()); return false;\" href=\"#\">Close</a>");
		resultBuffer.append("<br /><strong>").append(inKey).append("</strong><br />");
		resultBuffer.append("<textarea class='edittextarea' id='").append(textAreaId).append("'>");
		resultBuffer.append(err == ErrorUtils.ERROR_CODES.NO_ERROR?stringWrapper.getString():err.toString());
		resultBuffer.append("</textarea>");		
		
		resultBuffer.append("<div>");
		
		inMessage.setHtmlContent(resultBuffer.toString());
	}	
	
	public IMessage editText(CommonMessage inMessage){
		if(!validateData(inMessage, "key", "dest")) return inMessage;
		String key = inMessage.getData().get("key");
		
		getTextarea2Edit(inMessage, "Text", key, inMessage.getData().get("_locale"),
				"DBRequest","updateText");
		return inMessage;
	}	
	
	public IMessage editTemplate(CommonMessage inMessage){
		if(!validateData(inMessage, "key", "dest")) return inMessage;
		String key = inMessage.getData().get("key");
		
		getTextarea2Edit(inMessage, "Template", key, "content",
				"DBRequest", "updateTemplate");
		return inMessage;
	}	
	
	public IMessage updateText(CommonMessage inMessage){
		if(!validateData(	inMessage, "key", "value", "dest")) return inMessage;
		
		String key = inMessage.getData().get("key");
		String value = inMessage.getData().get("value");		
				
		update(inMessage, "Text", key, inMessage.getData().get("_locale"), value);
		
		return inMessage;
	}
	
	private void update(
			CommonMessage inMessage, 
			String inCFName, 
			String inKey, 
			String inColumnName, 
			String inValue){
		
		ErrorUtils.ERROR_CODES err = DBUtils.setValue(inMessage.getData().get("appid"), inCFName, inKey, inColumnName, inValue);
		
		if(err != ErrorUtils.ERROR_CODES.NO_ERROR){
			getLog().error(err.toString());
			inMessage.setError(err.toString());
			return ;
		}		
		
		StringWrapper outValue = new StringWrapper();
		err = DBUtils.getValue(inMessage.getData().get("appid"), inCFName, inKey, inColumnName, outValue);
		
		if(err != ErrorUtils.ERROR_CODES.NO_ERROR){
			getLog().error(err.toString());
			inMessage.setError(err.toString());
			return;
		}
		
		ADeployer.deployContent(outValue.getString(), inMessage);
	}
	
	public IMessage updateTemplate(CommonMessage inMessage){
		if(!validateData(	inMessage, "key", "value", "dest")) return inMessage;
		
		String key = inMessage.getData().get("key");
		String value = inMessage.getData().get("value");		
		
		update(inMessage, "Template", key, "content", value);
		
		return inMessage;
	}	
}
