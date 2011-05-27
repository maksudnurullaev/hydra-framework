package org.hydra.messages.handlers;


import java.util.ArrayList;
import java.util.List;

import org.hydra.deployers.Deployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

public class AdmUsers extends AMessageHandler {

	public IMessage getUsersFor(CommonMessage inMessage){
		if(!testData(inMessage, "key")) return inMessage;
		
		String content  = String.format("[[System|Application|%s|Users]]", inMessage._web_application.getId());
		getLog().debug("Try to get content for: " + content);
		
		List<String> links = new ArrayList<String>();
		String htmlContent = Deployer.deployContent(content,inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
		inMessage.setHtmlContent(htmlContent);
		
		return inMessage;
	}	
}
