package org.hydra.messages.handlers;


import org.hydra.deployers.ADeployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.ERROR_CODES;
import org.hydra.utils.Utils;

public class AdmTags extends AMessageHandler {

	public IMessage getTagsFor(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Tags|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
		
		return(ADeployer.deployContent(content,inMessage));
	}
	
	public IMessage newForm(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		StringBuffer result = new StringBuffer();
		
		result.append(Utils.T("template.html.custom.input.ID.Value.MaxWdth.Wdth", 
				"tag.new", 
				"",
				"50",
				"50"));
		result.append("<br />");
		
		String jsData = String.format("handler:%s,action:%s,appid:%s,dest:%s, value:%s"
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
		
		return(ADeployer.deployContent(result.toString(),inMessage));
	}

	public IMessage addTag(CommonMessage inMessage){
		if(!testData(inMessage, "appid", "value")) return inMessage;
		String appId = inMessage.getData().get("appid");
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
		if(!testData(inMessage, "appid", "value")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String value = inMessage.getData().get("value").trim();
				
		String inColumnFamily = "Tag";
		
		inMessage.getData().put("dest", "admin.app.action");
		ERROR_CODES errCode = DBUtils.deleteKey(appId, inColumnFamily, value);
		if(errCode != ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(getTagsFor(inMessage));
	}	
}
