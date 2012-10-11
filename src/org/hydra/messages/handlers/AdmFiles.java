package org.hydra.messages.handlers;

import java.io.File;
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
		if(!validateData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Files|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
						
		return(ADeployer.deployContent(content,inMessage));
	}	
	
	public IMessage addForm(CommonMessage inMessage){
		if(!validateData(inMessage, "appid", "dest")) return inMessage;
		
		String form = getAddFrom(inMessage);
		
		return(ADeployer.deployContent(form,inMessage));		
	}	

	private String getAddFrom(CommonMessage inMessage){
		String appId = inMessage.getData().get("appid");
		String dest = inMessage.getData().get("dest");
		
		ArrayList<IField> fields = new ArrayList<IField>();
		FieldInput fileField = new FieldInput("New_File");
		fileField.setType("file");
		fields.add(fileField);
		
		String form = Utils.generateForm(
				String.format("<h4>[[Dictionary|Text|New_File|NULL]]</h4>"), appId, 
				"AdmFiles", "add", 
				"AdmFiles", "list", 
				dest, fields, null, inMessage);
		return(form);
	}
	
	public IMessage add(CommonMessage inMessage){			
		if(inMessage.getFile() == null){
			inMessage.setError("NO_FILE");
			inMessage.clearContent();
			return(inMessage);
		}
		
		String result  = FileUtils.saveFile(inMessage);
		result += getAddFrom(inMessage);
		
		return(ADeployer.deployContent(result,inMessage));		
	}

	public IMessage delete(CommonMessage inMessage){
		if(inMessage.getData().get("file_path") == null){
			inMessage.setError("File path not defined");
			return(inMessage);
		}
		File file = FileUtils.getRealFile(inMessage.getData().get("file_path"));
		file.delete();
		return(list(inMessage));		
	}
}
