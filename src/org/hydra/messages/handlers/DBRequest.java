package org.hydra.messages.handlers;

import java.util.Map;

import org.hydra.deployers.ADeployer;
import org.hydra.deployers.System;
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
			String inCName, // locale
			String inAction,
			String inActionMethod){
		
		String appId = inMessage.getData().get("appid");
		String spanId = String.format("%s.%s", inCFName, inKey); 
		String textAreaId = Utils.sanitazeHtmlId(String.format("%s.%s.%s.textarea", appId, inCFName, inKey));
		
		String jsData = Utils.jsData(
					"appid", Utils.Q(appId)
					, "handler", Utils.Q(inAction)
					, "action", Utils.Q(inActionMethod)
					, "key", Utils.Q(inKey)
					, "value", String.format("jQuery('#%s').prop('value')",textAreaId)
					, "dest", Utils.Q(Utils.sanitazeHtmlId(spanId))
				);
		
		StringWrapper stringWrapper = new StringWrapper();		
		ErrorUtils.ERROR_CODES err = DBUtils.getValue(inMessage.getData().get("appid"), inCFName, inKey, inCName, stringWrapper);
		
		if( err == ERROR_CODES.NO_ERROR){
			String str = stringWrapper.getString();
			str = replaceTextareaEndTagForEdit(str);
			stringWrapper.setString(str);
		} else {
			stringWrapper.setString(err.toString()); 
		}		
		
		StringBuffer resultBuffer = new StringBuffer("<div class=\"edit\">");

		if(inCFName.equalsIgnoreCase("TEXT")){
			resultBuffer.append(getAllLocales2Update(inMessage, inCName, appId, textAreaId, inKey));
			resultBuffer.append("<br />");
		}
		
		resultBuffer.append("<strong>").append(inCFName).append(": </strong>").append(inKey);
		resultBuffer.append("<br />");
		
		resultBuffer.append(Utils.T("template.html.a.onClick.sendMessage.Label", jsData, "Upload"));
		resultBuffer.append(" | <a onclick=\"javascript:void(Globals.clearEditArea()); return false;\" href=\"#\">Close</a>");
		resultBuffer.append("<br />");
		
		resultBuffer.append("<textarea class='edittextarea' id='").append(textAreaId).append("'>");
		resultBuffer.append(stringWrapper.getString());
		resultBuffer.append("</textarea>");		
		
		resultBuffer.append("<div>");
		
		inMessage.setHtmlContent(resultBuffer.toString());
	}

	public IMessage getText4Locale(CommonMessage inMessage){
		StringWrapper stringWrapper = new StringWrapper();		
		ErrorUtils.ERROR_CODES err = DBUtils.getValue(inMessage.getData().get("appid")
				, "Text"
				, inMessage.getData().get("key")
				, inMessage.getData().get("locale")
				, stringWrapper);
		
		if( err == ERROR_CODES.NO_ERROR){
			String str = stringWrapper.getString();
			str = replaceTextareaEndTagForEdit(str);
			stringWrapper.setString(str);
		} else {
			stringWrapper.setString(err.toString()); 
		}		
		
		inMessage.setHtmlContent(stringWrapper.getString());
		
		return(inMessage);
	}
	
	private String getAllLocales2Update(
			CommonMessage inMessage
			, String inLocaleName
			, String inAppId
			, String inTextAreaId
			, String inKey
			) 
	{		
		String jsData = Utils.jsData(
				"appid", Utils.Q(inAppId)
				, "handler", Utils.Q("DBRequest")
				, "locale", Utils.Q("%s")
				, "action", Utils.Q("getText4Locale")
				, "key", Utils.Q(inKey)
				, "dest", Utils.Q(Utils.sanitazeHtmlId(inTextAreaId))
			);
		String result = "";
		Map<String, String> localesMap = System.getAppDefinedLocales(inMessage);
		for (Map.Entry<String, String> entry : localesMap.entrySet()) {
			String locale = entry.getKey();
			if(!result.isEmpty()) result += " | ";
			String label = locale;
			if(locale.equals(inLocaleName)){
				label = "&#8595;" + locale + "&#8595;";
			}
			result += Utils.T("template.html.a.onClick.sendMessage.Label", String.format(jsData, locale), label);
		}
		return(result);
}

	private String replaceTextareaEndTagForEdit(String inString) {
		return (Utils.replaceAll(inString));
	}	
	
	public IMessage editText(CommonMessage inMessage){
		if(!validateData(inMessage, "key", "dest")) return inMessage;
		
		getTextarea2Edit(inMessage
				, "Text"
				, inMessage.getData().get("key")
				, inMessage.getData().get("_locale")
				, "DBRequest"
				, "updateText");
		
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
