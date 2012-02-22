package org.hydra.deployers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Roles;
import org.hydra.utils.Utils;

public class ApplicationImages {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.ApplicationImages");
	public static final String formatJSiBox = "<a href='%s' rel='rr' onclick='return jsiBoxOpen(this)' title='%s'><img src='%s' /></a> ";

	public static String getKeyHow(
			String inKey, 
			String inHow,
			IMessage inMessage) {
		if(inHow.compareToIgnoreCase("jsibox") == 0)
			return getImagesJSIBox(inKey, inMessage);
		_log.error("Could not find HOW part: " + inHow);
		return "Could not find HOW part: " + inHow;		 
	}

	private static String getImagesJSIBox(
			String inFolder, 
			IMessage inMessage) {
		String appid = inMessage.getData().get("_appid");
		_log.debug("Images as jsibox: ");
		_log.debug("App id: " + appid);
		_log.debug("Folder: " + inFolder);
		
		boolean isUserEditor = Roles.roleNotLessThen(Roles.USER_EDITOR, inMessage);
		String imageGalleryDivId = inFolder.replace('/', '_');
		StringBuffer content = new StringBuffer(String.format("<div class='gallery' id='%s'>", imageGalleryDivId));
		
		List<String> fileURLs = new ArrayList<String>();		
		FileUtils.getListOfFiles4Dir(
				String.format(FileUtils.URL4FILES_APPID_SUBFOLDER, appid, inFolder),
				fileURLs,
				null,
				false);		

		String jsData = Utils.jsData(
				 "handler", Utils.Q("GalleryImages")
				,"action",  Utils.Q("editImageTags")
				,"imagePath", Utils.Q("%s")
				,"thumbImagePath", Utils.Q("%s")
				,"dest", Utils.Q(imageGalleryDivId)
			);			
		String editLink = Utils.createJSLink(jsData, "Edit");
		
		for (String filePath : fileURLs) {
			if(FileUtils.isImage(filePath)){
				int index = filePath.lastIndexOf("/");
				String thumbPath = filePath.substring(0, index) + "/thumbs/" + filePath.substring(index+1);
				String fileDescription = filePath;
				if(isUserEditor){
					content.append("<span class='editable_image'>"
								+ createThumbLinkImage(filePath, fileDescription, thumbPath)
								+ " "
								+ String.format(editLink, filePath, thumbPath) 
								+ "</span>&nbsp;&nbsp;");
				} else {
					content.append(createThumbLinkImage(filePath, fileDescription, thumbPath));					
				}
			}
	    }
		
	    if(fileURLs.size() == 0)
	    	content.append("...");
		content.append("</div>");
		
		return(content.toString());
	}

	public static String createThumbLinkImage(String filePath, String fileDescription, String thumbFilePath) {
		return String.format(formatJSiBox, filePath, fileDescription, thumbFilePath);
	}

}
