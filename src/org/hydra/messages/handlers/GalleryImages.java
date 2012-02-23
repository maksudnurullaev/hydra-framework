package org.hydra.messages.handlers;

import java.util.ArrayList;

import org.hydra.deployers.ADeployer;
import org.hydra.deployers.ApplicationImages;
import org.hydra.html.fields.FieldInput;
import org.hydra.html.fields.FieldTextArea;
import org.hydra.html.fields.IField;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.ImageUtils;
import org.hydra.utils.Utils;

public class GalleryImages extends AMessageHandler {
	public final static String format = "Application|Images|%s|jsibox";	
	public final static String Description = "Description";
	
	public IMessage list(CommonMessage inMessage){
		String destDivId = inMessage.getData().get("dest");
		String galleryPath = destDivId.replace('_', '/');
		String hakdContent = String.format(format, galleryPath);
		inMessage.getData().put("hakdContent", hakdContent);
		return(General.getHAKDContent(inMessage));
	}

	public IMessage updateDescription(CommonMessage inMessage){
		if(!validateData(inMessage, "imagePath", "thumbImagePath", Description)) return inMessage;
		String imagePath = Utils.getRealPath(inMessage.getData().get("imagePath"));
		
		FileUtils.saveFileDescriptions(inMessage, 
				imagePath, 
				Description);
		return(editImageTags(inMessage));
	}
	
	public IMessage editImageTags(CommonMessage inMessage){
		if(!validateData(inMessage, "imagePath", "thumbImagePath")) return inMessage;
		String imagePath = inMessage.getData().get("imagePath");
		String thumbImagePath = inMessage.getData().get("thumbImagePath");
		String destDivId = inMessage.getData().get("dest");

		String imageDescription = ImageUtils.getImageDescription(imagePath);
		
		// create thumb image node
		String thumbImageNode = ApplicationImages.createThumbLinkImage(imagePath, imageDescription, thumbImagePath);
		
		// create form to edit image description
		ArrayList<IField> fields = new ArrayList<IField>();
		fields.add(new FieldTextArea(Description, imageDescription, "style=\"width: 25em; height: 5em; border: 1px solid #7F9DB9;\""));
		
		FieldInput hiddenImagePath = new FieldInput("imagePath", imagePath);
		hiddenImagePath.setType("hidden");
		fields.add(hiddenImagePath);

		FieldInput hiddenThumbImagePath = new FieldInput("thumbImagePath", thumbImagePath);
		hiddenThumbImagePath.setType("hidden");
		fields.add(hiddenThumbImagePath);
		
		String form = Utils.generateForm(
				String.format("<h4>[[DB|Text|Edit_Description|span]]</h4>"), null, 
				"GalleryImages", "updateDescription", 
				"GalleryImages", "list", 
				destDivId, fields, null, inMessage);
		
		inMessage = (CommonMessage) ADeployer.deployContent(form,inMessage);		
		
		// set final html content
		inMessage.setHtmlContent(
				thumbImageNode
				+ "<hr />"
				+ inMessage.getHtmlContents().get(destDivId));
		
		return (inMessage);
	}	

}
