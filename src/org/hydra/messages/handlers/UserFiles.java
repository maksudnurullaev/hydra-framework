package org.hydra.messages.handlers;

import org.hydra.deployers.ADeployer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils;
import org.hydra.utils.FileUtils;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.StringWrapper;
import org.hydra.utils.Utils;

public class UserFiles extends AMessageHandler {

	public IMessage list(CommonMessage inMessage){
//		if(!testData(inMessage, "appid")) return inMessage;
//		String appId = inMessage.getData().get("appid");
//		
//		String content  = String.format("[[Application|Files|%s|html]]", appId);
//		getLog().debug("Try to get content for: " + content);
//						
//		return(ADeployer.deployContent(content,inMessage));
		inMessage.setError("Not implementer yet!");
		return (inMessage);
	}	
	
	public IMessage add(CommonMessage inMessage){
		if(!SessionUtils.isCaptchaVerified(inMessage)){
			return inMessage;
		}
		if(!validateData(inMessage, "appid", "folder")) return inMessage;
		if(inMessage.getFile() == null){
			inMessage.setError("NO_FILE");
			inMessage.clearContent();
			return(inMessage);
		}
		
		StringWrapper filePath = new StringWrapper();
		
		String returnFormat = "";
		if(FileUtils.saveFileAndDescriptions(inMessage, filePath, "Name", "Public", "Tag", "Text"))
		{
			returnFormat = "[[DB|Template|FileSavedOk.Header|span]]";
			String fullPath = getMainUrl(inMessage.getUrl()) + filePath.getString();
			returnFormat += "[[DB|Text|PathAsText|span]] " + fullPath;
			returnFormat += "[[DB|Text|PathAsLink|span]] " + Utils.T("template.html.a.Href.Label", fullPath, fullPath);
			returnFormat += "[[DB|Template|FileSavedOk.Footer|span]]";
			
		}else{
			returnFormat = "[[DB|Template|FileSavedFailed|span]]";
			inMessage.setError(filePath.getString());
		}	
		// finish
		
		return (ADeployer.deployContent(returnFormat, inMessage));
	}

	private static String getMainUrl(String inUrl) {
		int result = inUrl.indexOf('/', 8);
		if(result != -1)
			return(inUrl.substring(0, result));
		return(inUrl);
	}

	public IMessage delete(CommonMessage inMessage){
		if(!validateData(inMessage, "appid", "key")) return inMessage;
		String appId = inMessage.getData().get("appid");
		String key = inMessage.getData().get("key");
				
		ErrorUtils.ERROR_CODES errCode = DBUtils.deleteKey(appId, "Template", key);
		if(errCode != ErrorUtils.ERROR_CODES.NO_ERROR){
			inMessage.setError(errCode.toString());
			return inMessage;
		}
				
		return(list(inMessage));		
	}
}
