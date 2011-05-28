package org.hydra.messages.handlers;


import java.util.ArrayList;
import java.util.List;

import org.hydra.deployers.ADeployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FieldInput;
import org.hydra.utils.IField;
import org.hydra.utils.Utils;

public class AdmUsers extends AMessageHandler {

	public IMessage getUsersFor(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Users|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
		
		List<String> links = new ArrayList<String>();
		String htmlContent = ADeployer.deployContent(content,inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
		inMessage.setHtmlContent(htmlContent);
		
		return inMessage;
	}	
	
	public IMessage newForm(CommonMessage inMessage){
		if(!testData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		ArrayList<IField> fields = new ArrayList<IField>();
		fields.add(new FieldInput("user_mail", ""));
		fields.add(new FieldInput("user_password", ""));
		fields.add(new FieldInput("user_password2", ""));
		
		String form = Utils.generateForm(appId, 
				"AdmUsers", "add", 
				"AdmUsers", "getUsersFor", 
				"admin.app.action", fields);
		
		List<String> links = new ArrayList<String>();
		String htmlContent = ADeployer.deployContent(form,inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));	
		return inMessage;		
	}	
}
