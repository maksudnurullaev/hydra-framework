package org.hydra.messages.handlers;

import java.util.ArrayList;

import org.hydra.deployers.ADeployer;
import org.hydra.html.fields.FieldInput;
import org.hydra.html.fields.IField;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Utils;

public class AdmFiles extends AMessageHandler {

	public IMessage list(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Files|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
						
		return(ADeployer.deployContent(content,inMessage));
	}	
	
	public IMessage addForm(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appID = inMessage.getData().get("appid");
		
		ArrayList<IField> fields = new ArrayList<IField>();
		FieldInput fileField = new FieldInput("input_file");
		fileField.setType("file");
		fields.add(fileField);
		
		String form = Utils.generateForm(
				String.format("<h4>[[DB|Text|New_File|span]]</h4>"), appID, 
				"AdmFiles", "add", 
				"AdmFiles", "list", 
				"admin.app.action", fields, null);
		form += "<div id=\"action_result\"></div>";
		
		return(ADeployer.deployContent(form,inMessage));		
	}	
	
	public IMessage add(CommonMessage inMessage){
		String appId = inMessage.getData().containsKey("appid")
			?  inMessage.getData().get("appid") : inMessage._web_application.getId();
			
		if(inMessage.getFile() == null){
			inMessage.setError("NO_FILE");
			inMessage.clearContent();
			return(inMessage);
		}
		String result  = FileUtils.saveFile4Admin(
				inMessage._web_context.getServletContext(), 
				appId, 
				inMessage.getFile());
		inMessage.setHtmlContents("action_result", result);
		// finish
		return (inMessage);
	}

	public IMessage delete(CommonMessage inMessage){
		if(!testData(inMessage, "key")) return inMessage;
		
		String filePath = inMessage.getData().get("key");
		
		String realPath = inMessage._web_context.getServletContext().getRealPath(filePath);		
		
		org.apache.cassandra.io.util.FileUtils.delete(realPath);
				
		return(list(inMessage));		
	}
}
