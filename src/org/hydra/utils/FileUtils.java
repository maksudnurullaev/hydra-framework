package org.hydra.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.abstracts.APropertyLoader;
import org.hydra.messages.CommonMessage;

public final class FileUtils {
	public static final int FILE_TYPE_UNKNOWN = 0;
	public static final int FILE_TYPE_IMAGE = 1;
	public static final int FILE_TYPE_COMPRESSED = FILE_TYPE_IMAGE << 1;
	public static final String URL4FILES_APPID_FILES = "files/%s/files/"; 
	public static final String URL4FILES_APPID_IMAGE = "files/%s/image/"; 
	public static final String FILE_DESCRIPTION_TEXT = "Text"; 
	public static final String FILE_DESCRIPTION_PUBLIC = "Public"; 

	private static final Log _log = LogFactory.getLog("org.hydra.utils.FileUtils");	
	public static final String generalImageFormat = "png";

	public static String saveImage4(ServletContext servletContext, String inAppId, BufferedImage inImage){
		// 0. Generate pathname for new image
		String uri4Image = Utils.F("files/%s/image/%s.%s", inAppId, RandomStringUtils.random(8,true,true), generalImageFormat);
		String realPath = servletContext.getRealPath(uri4Image);
		// 1. Save image in PNG formate
		File output = new File(realPath);
		try {
			ImageIO.write(inImage, generalImageFormat, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// finish
		return (uri4Image);
	}
	
	public static void getListOfFiles4Dir(
			String URL,
			List<String> result,
			String ifFileNameEndWith) {
		
		if(!URL.endsWith("/")) URL += "/";
		
		String realURI = Utils.getServletContent().getRealPath(URL);
		
		if(realURI == null) return;
			
		File dir = new File(realURI);
		if(dir.isDirectory() && dir.list() != null){
			for(String path2File: dir.list()){
				File file = new File(realURI, path2File);
				if(file.isDirectory()){
					getListOfFiles4Dir(URL + path2File, result, ifFileNameEndWith);
				}else if(file.isFile()){
					if(ifFileNameEndWith == null || file.getName().endsWith(ifFileNameEndWith)){
							result.add(URL + path2File);
					}
				}
			}
		}
	}

	public static String saveFile4Admin(
			ServletContext servletContext, 
			String inAppId,
			FileTransfer file) {
		// Generate pathname for new image
		String uri4File = Utils.F(URL4FILES_APPID_FILES + "%s", inAppId, file.getFilename());
		String realPath = servletContext.getRealPath(uri4File);
		String resultStr = "";
		// 1. 
		InputStream is = null;
		FileOutputStream os = null;
		byte[] bufer = new byte[4096];
		int bytesRead = 0;
		try {
			is = file.getInputStream();
			os = new FileOutputStream(realPath);
			while((bytesRead = is.read(bufer)) != -1){
				_log.debug("bytesRead: " + bytesRead);
				os.write(bufer, 0, bytesRead);
			}
			os.close();
			resultStr = getFileBox(inAppId, uri4File);
		} catch (Exception e) {
			_log.error(e.toString());
			resultStr = e.toString();
		}	
		// finish
		return (resultStr);		
	}
	
	public static boolean saveFile(CommonMessage inMessage,
			StringWrapper outFilePath, String ... dataDescriptionKeys) {
		ServletContext servletContext = inMessage._web_context.getServletContext();
		String inAppId = inMessage._web_application.getId();
		FileTransfer file = inMessage.getFile();
		boolean result = false;
		// 0. Generate pathname for new image
		String uri4FilePath;
		String orginalFileName = sanitize(file.getFilename());
		
		uri4FilePath = Utils.F(URL4FILES_APPID_FILES + "%s", inAppId, getMD5FileName(orginalFileName) + getFileExtension(orginalFileName));
		
		result = saveFile(servletContext.getRealPath(uri4FilePath), file);
		result = saveFileDescriptions(inMessage, servletContext.getRealPath(uri4FilePath), orginalFileName, dataDescriptionKeys);
			
		if(result)
			outFilePath.setString(String.format("%s/%s", servletContext.getContextPath(), uri4FilePath));
		
		return result;
	}	
	
	public static String getMD5FileName(String pass) {
		String result;
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			byte[] data = pass.getBytes(); 
			m.update(data,0,data.length);
			BigInteger i = new BigInteger(1,m.digest());
			result = String.format("%1$032X", i);
		} catch (NoSuchAlgorithmException e) {
			_log.error(e.getMessage());
			result = Utils.GetUUID();
		}
		return(result);
	}	

	public static boolean saveFileDescriptions(CommonMessage inMessage,
			String filePath, String orginalFileName, String ... dataDescriptionKeys) {
		if(dataDescriptionKeys.length == 0)return(false);
		
		try {
			File file = new File(filePath + APropertyLoader.SUFFIX);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), Constants._utf_8));
			for(int i = 0; i < dataDescriptionKeys.length ; i++){
				if(inMessage.getData().containsKey(dataDescriptionKeys[i]))
				{
					String key = dataDescriptionKeys[i];
					String value = inMessage.getData().get(key).trim();
					if(key.compareToIgnoreCase("Name") == 0) value = sanitize(value);
					else if(!value.isEmpty()) value = value.replaceAll("\n", "\n\t");
					if(value.length() > Constants._max_textarea_field_limit)
						value = value.substring(0, Constants._max_textarea_field_limit - 3 ) + "...";
					if(value.length() > 0) bw.write(key + " = " + value + "\n");
				}					
			}
			bw.close();			
		} catch (IOException e) {
			_log.error(e.getMessage());
			return(false);
		}
		return(true);
	}
	
	public static String getFileExtension(String filename){
		int lastDot = filename.lastIndexOf('.');
		if(lastDot == -1) return(null);		
		return(filename.substring(lastDot));
	};
	
	public static String sanitize(String filename) {
		if(filename == null || filename.isEmpty()) return Utils.GetUUID();
		int lastLeft = filename.lastIndexOf("\\");
		int lastRight = filename.lastIndexOf("/");
		if(lastLeft == lastRight){ // not found!
			return(filename);
		}
		return filename.substring((lastLeft < lastRight ? lastRight : lastLeft) + 1);
	}
	
	private static boolean saveFile(String realPathFile, FileTransfer file) {
		InputStream is = null;
		FileOutputStream os = null;		
		byte[] bufer = new byte[4096];
		int bytesRead = 0;
		try {
			is = file.getInputStream();
			os = new FileOutputStream(realPathFile);			
			while((bytesRead = is.read(bufer)) != -1){
				_log.debug("bytesRead: " + bytesRead);
				os.write(bufer, 0, bytesRead);
			}
			os.close();
			return(true);
		} catch (Exception e) {
			_log.error(e.toString());
			return(false);
		}	
	}
	
	public static String getFileBox(String inAppID, String filePath) {
		StringBuffer content = new StringBuffer();
		String mimeType = URLConnection.guessContentTypeFromName(filePath);
		if(mimeType == null) mimeType = "unknown";
		
    	String divHiddenID = "template." + filePath;  
		content.append("<div style=\"margin: 5px; padding: 5px; border: 1px solid rgb(127, 157, 185);\">");
		
    	content.append(getDeleteLink("AdmFiles", Utils.Q("admin.app.action"), inAppID, filePath) + " ");
    	content.append("[<strong>" + mimeType + "</strong>] ");
    	
    	String htmlTag = "NOT_DEFINED";
		if(mimeType.compareToIgnoreCase("image") >= 0){
			htmlTag = Utils.F("<img src=\"%s\" border=\"0\">", filePath);
		}else{			
			htmlTag = Utils.F("<a href=\"%s\" target=\"_blank\">TEXT</a>", filePath);
		}
		content.append(Utils.toogleLink(divHiddenID, filePath));
		content.append(Utils.F("<div id=\"%s\" style=\"display: none;\" class=\"edit\">%s<hr />%s</div>", 
				divHiddenID,
				StringEscapeUtils.escapeHtml(htmlTag),
				htmlTag));        	
    	
    	content.append("</div>");
    	
		return content.toString();
	}

	public static String getDeleteLink(
			String inHandler,
			String inDest,
			String inAppID, 
			String key) {
		String jsData = Utils.jsData(
				 "handler", Utils.Q(inHandler)
				,"action",  Utils.Q("delete")
				,"appid", Utils.Q(inAppID)
				,"key", Utils.Q(key)
				,"dest", inDest
			);
		return(Utils.F("[%s]", Utils.createJSLinkWithConfirm("Delete",jsData, "X")));		
	}

	public static String getFilePropertiesDescription(String inAppID,
			String inUserID, String propertiesFilePath) {
		boolean isAdmin = Roles.roleNotLessThen(Roles.USER_ADMINISTRATOR, inAppID, inUserID);
		Properties properties = parseProperties(propertiesFilePath);
		String Public = properties.getProperty(FILE_DESCRIPTION_PUBLIC);
    	boolean isPublic = ((Public != null) 
    			&& (Public.compareToIgnoreCase("true") == 0)) ? true : false;
		
    	String divHiddenID = "template." + sanitize(propertiesFilePath);
    	String Description = properties.getProperty(FILE_DESCRIPTION_TEXT);
    	if(isPublic || isAdmin){
			StringBuffer content = new StringBuffer();
			content.append("<div class=\"file_row\">");
			
			//TODO delete link
	    	// if(isAdmin)
	    	//	content.append(getDeleteLink("UserFiles", Utils.Q(Constants._user_content), inAppID, propertiesFilePath) + " ");
			
			// name
			content.append("<span class=\"file_name\">" + properties.get("Name") + "</span>");
			content.append("<br />");
	    	// download link
			String htmlTag = Utils.F("&nbsp;&nbsp;<a href=\"%s\" target=\"_blank\">%s</a>", 
					stripPropertiesExtension(propertiesFilePath),
					"[[DB|Text|Download|locale]]");
			content.append(htmlTag);
			content.append(" ");
			// description
			if(Description != null){ 
				content.append(Utils.toogleLink(divHiddenID, "[[DB|Text|Description|locale]]"));
				content.append(Utils.F("<div id=\"%s\" class=\"file_description\" >%s</div>", 
						divHiddenID,
						properties.get(FILE_DESCRIPTION_TEXT)));
			}
	
	    	content.append("</div>");
	    	
			return content.toString();
    	}
    	return("");
	}

	public static String stripPropertiesExtension(String propertiesFilePath) {
		return propertiesFilePath.substring(0, propertiesFilePath.length() - APropertyLoader.SUFFIX.length());
	}

	public static Properties parseProperties(String propertiesFilePath) {
		ServletContext servletContext = Utils.getServletContent();
		Properties properties = APropertyLoader.parsePropertyFile(servletContext.getRealPath(propertiesFilePath));
		return properties;
	}

	public static String getFromHtmlFile(String inAppId, String fileName) {
		ServletContext servletContext = Utils.getServletContent();
		String filePath = String.format("/files/%s/html/%s.html", inAppId, fileName);
		String realPath = servletContext.getRealPath(filePath);
		String content = null;
		if(realPath != null){
			File file = new File(realPath);
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis,Constants._utf_8));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (!line.trim().isEmpty()){
						if(content == null) content = line;
						else content += line;
					}
				}
			} catch (IOException e) {
			}			
		}
		return(content); 
	}
}
