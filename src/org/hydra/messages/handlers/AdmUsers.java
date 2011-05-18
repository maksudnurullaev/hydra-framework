package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

public class AdmUsers extends AMessageHandler { // NO_UCD	

	public static IMessage getUsers4(CommonMessage inMessage) {
		if(!testData(inMessage, "key")) return inMessage;
		
		String appId = inMessage.getData().get("key");
		
		if(!appId.isEmpty()){
			List<String> links = new ArrayList<String>();
			String content = String.format("[[System|Users|%s|options]]", appId);
			String htmlContent = Utils.deployContent(content,inMessage, links);
			inMessage.setHtmlContent(htmlContent);
			inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));									
		} else {
			inMessage.setHtmlContent("...");
		}
		return inMessage;
	}

}
