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
		if(!validateData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		
		String content  = String.format("[[Application|Files|%s|html]]", appId);
		getLog().debug("Try to get content for: " + content);
						
		return(ADeployer.deployContent(content,inMessage));
	}	
	
	public IMessage addForm(CommonMessage inMessage){
		if(!validateData(inMessage, "appid")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String dest = inMessage.getData().get("dest");
		
		ArrayList<IField> fields = new ArrayList<IField>();
		FieldInput fileField = new FieldInput("input_file");
		fileField.setType("file");
		fields.add(fileField);
		
		String form = Utils.generateForm(
				String.format("<h4>[[DB|Text|New_File|span]]</h4>"), appId, 
				"AdmFiles", "add", 
				"AdmFiles", "list", 
				dest, fields, null, inMessage);
		
		return(ADeployer.deployContent(form,inMessage));		
	}	
	
	public IMessage add(CommonMessage inMessage){			
		if(inMessage.file == null){
			inMessage.setError("NO_FILE");
			inMessage.clearContent();
			return(inMessage);
		}
		
		String result  = FileUtils.saveFile(inMessage);
		inMessage.setHtmlContent(result);
		
		// finish
		return (inMessage);
	}

	public IMessage delete(CommonMessage inMessage){
		if(inMessage.fileRealPath == null){
			inMessage.setError("File path not defined");
			return(inMessage);
		}
		getLog().debug("Delete file: " + inMessage.fileRealPath);
		org.apache.cassandra.io.util.FileUtils.delete(inMessage.fileRealPath);				
		return(list(inMessage));		
	}
}
