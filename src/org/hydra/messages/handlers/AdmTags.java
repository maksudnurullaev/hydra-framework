package org.hydra.messages.handlers;


import java.util.ArrayList;
import java.util.List;

import org.hydra.deployers.Deployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.ERROR_CODES;
import org.hydra.utils.Utils;

public class AdmTags extends AMessageHandler {

	public IMessage getTagsFor(CommonMessage inMessage){
		if(!testData(inMessage, "key")) return inMessage;
		String appId = inMessage.getData().get("key");
		
		String content  = String.format("[[System|Application|%s|Tags]]", appId);
		getLog().debug("Try to get content for: " + content);
		
		List<String> links = new ArrayList<String>();
		String htmlContent = Deployer.deployContent(content,inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
		inMessage.setHtmlContent(htmlContent);
		
		return inMessage;
	}
	
	public IMessage newTagForm(CommonMessage inMessage){
		if(!testData(inMessage, "key")) return inMessage;
		String appId = inMessage.getData().get("key");
		
		StringBuffer result = new StringBuffer();
		
		result.append(Utils.T("template.html.custom.input.ID.Value.MaxWdth.Wdth", 
				"tag.new", 
				"",
				"50",
				"50"));
		result.append("<br />");
		
		String jsData = String.format("handler:%s,action:%s,key:%s,dest:%s, value:%s"
				, Utils.Q("AdmTags")
				, Utils.Q("addTag")
				, Utils.Q(appId)
				, Utils.Q("admin.app.action.tag")
				, "$('tag.new').value");
		
		result.append(Utils.T("template.html.a.onClick.sendMessage.Label"
				, jsData
				, "Save"));
		

		result.append(" | ");
		result.append(Utils.createJSLinkHAKD(
				  Utils.Q("AdmTags") 
				, Utils.Q("getTagsFor") 
				, Utils.Q(appId) 
				, Utils.Q("admin.app.action") 
				, "Cancel"
				)
			);			
		
		inMessage.setHtmlContent(result.toString());
		return inMessage;		
	}

	public IMessage addTag(CommonMessage inMessage){
		if(!testData(inMessage, "key", "value")) return inMessage;

		String appId = inMessage.getData().get("key");
		String value = inMessage.getData().get("value").trim();
		
		if(value.isEmpty()){
			inMessage.setError("NO data");
			return inMessage;
		}
		
		String inColumnFamily = "Tag";
		String inKey = "Tag." + value;
		
		inMessage.getData().put("dest", "admin.app.action");
		DBUtils.setValue(appId, inColumnFamily, inKey, "name", value);
				
		return(getTagsFor(inMessage));
	}
	
	public IMessage deleteTag(CommonMessage inMessage){
		if(!testData(inMessage, "key", "value")) return inMessage;
		
		String appId = inMessage.getData().get("key");
		String tagKey = inMessage.getData().get("value").trim();
				
		String inColumnFamily = "Tag";
		
		inMessage.getData().put("dest", "admin.app.action");
		ERROR_CODES errCode = DBUtils.deleteKey(appId, inColumnFamily, tagKey);
		if(errCode != ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(getTagsFor(inMessage));
	}	
}
