package org.hydra.messages.handlers;

import org.hydra.deployers.ApplicationImages;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Utils;

public class GalleryImages extends AMessageHandler {

	public IMessage editImageTags(CommonMessage inMessage){
		if(!validateData(inMessage, "imagePath", "thumbImagePath")) return inMessage;
		String filePath = inMessage.getData().get("imagePath");
		String thumbFilePath = inMessage.getData().get("thumbImagePath");
		String dest = inMessage.getData().get("dest");
		String galleryPath = dest.replace('_', '/');
		String parentContent = "Application|Images|" + galleryPath + "|jsibox";
		
		
		getLog().error("Image path: " + filePath);
		getLog().error("Thumb image path: " + thumbFilePath);
		
		
		String jsData = Utils.jsData(
				 "handler", Utils.Q("General")
				,"action",  Utils.Q("getHAKDContent")
				,"hakdContent", Utils.Q(parentContent)
				,"thumbImagePath", Utils.Q("%s")
				,"dest", Utils.Q(dest)
			);			
		String goBackLink = Utils.createJSLink(jsData, "Back to gallery");		
		
		inMessage.setHtmlContent(
				ApplicationImages.createThumbLinkImage(filePath, "fileDescription", thumbFilePath)
				+ "<br />"
				+ goBackLink);
		
		return (inMessage);
	}	

}
