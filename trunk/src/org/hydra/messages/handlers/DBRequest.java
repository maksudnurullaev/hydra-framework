package org.hydra.messages.handlers;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

public class DBRequest extends AMessageHandler{ // NO_UCD

	public void getTextarea2Edit(
			CommonMessage inMessage,
			String inCFName,
			String inColumnName, 
			String updateHandlerName,
			String updateHandlerMethodName){
		
		String cfBeanName = "cf" + inMessage._web_application.getId() + inCFName;	
		String key = inMessage.getData().get("key");
		
		_log.debug("Edit action for:" + cfBeanName);
		_log.debug("... key:" + key);
		_log.debug("... columnName:" + inColumnName);
		
		Result result = new Result();
		BeansUtils.getWebContextBean(result, cfBeanName);
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			return;
		}
		
		SimpleCassandraDao dbCf = (SimpleCassandraDao) result.getObject();
		
		StringBuffer resultBuffer = new StringBuffer();
		
		resultBuffer.append("<sup class='editorlinks' id='").append(key).append(".editorlinks").append("'>");
		resultBuffer.append("&nbsp;<a onclick=\"javascript:void(Globals.uploadIt('").append(key).append("','").append(updateHandlerName).append("','").append(updateHandlerMethodName).append("')); return false;\" href=\"#\">Upload</a>");
		resultBuffer.append(" | <a onclick=\"javascript:void(Globals.showEditorLinks()); return false;\" href=\"#\">Close</a>");
		
		resultBuffer.append(" </sup><br/><sup>");
		resultBuffer.append("&nbsp;<strong>");
		resultBuffer.append(key);
		resultBuffer.append("  </strong>");		
		resultBuffer.append(" </sup><br/>");
		
		resultBuffer.append("<textarea class='edittextarea' id='").append(key).append(".textarea'>");
		resultBuffer.append(dbCf.get(key, inColumnName));
		resultBuffer.append("</textarea>");
		
		inMessage.setHtmlContent(resultBuffer.toString());
	}	
	
	public IMessage editText(CommonMessage inMessage){
		if(!testParameters(	inMessage, "key", "dest")) return inMessage;
		
		getTextarea2Edit(inMessage, "Text", inMessage._locale,
				"DBRequest","updateText");
		return inMessage;
	}	
	
	public IMessage editTemplate(CommonMessage inMessage){
		if(!testParameters(	inMessage, "key", "dest")) return inMessage;
		
		getTextarea2Edit(inMessage, "Template", "html",
				"DBRequest", "updateTemplate");
		return inMessage;
	}	
	
	public void update(
			CommonMessage inMessage,
			String inCFName,
			String inColumnName)
	{
		String cfBeanName = "cf" + inMessage._web_application.getId() + inCFName;
		String key = inMessage.getData().get("dest");
		String value = inMessage.getData().get("value");	
		
		_log.debug("Update action for:" + cfBeanName);
		_log.debug("... key:" + key);
		_log.debug("... columnName:" + inColumnName);
		_log.debug("... value length:" + value.length());
		
		Result result = new Result();
		BeansUtils.getWebContextBean(result, cfBeanName);
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			return;
		}
		
		SimpleCassandraDao dbCf = (SimpleCassandraDao) result.getObject();
		
		dbCf.insert(key, inColumnName, value);		
		inMessage.setHtmlContent(Utils.deployContent(dbCf.get(key, inColumnName), inMessage));
	}
	
	public IMessage updateText(CommonMessage inMessage){
		if(!testParameters(	inMessage, "value", "dest")) return inMessage;
		
		update(inMessage, "Text", inMessage._locale);
		return inMessage;
	}
	
	public IMessage updateTemplate(CommonMessage inMessage){
		if(!testParameters(	inMessage, "value", "dest")) return inMessage;
		
		update(inMessage, "Template", "html");
		return inMessage;
	}	
}
