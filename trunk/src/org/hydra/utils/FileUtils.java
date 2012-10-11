package org.hydra.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.beans.abstracts.APropertyLoader;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;

public final class FileUtils {
	public static final int FILE_TYPE_UNKNOWN = 0;
	public static final int FILE_TYPE_IMAGE = 1;
	public static final int FILE_TYPE_COMPRESSED = FILE_TYPE_IMAGE << 1;
	public static final String URL4FILES_APPID_SUBFOLDER = "files/%s/%s/"; 
	public static final String FILE_DESCRIPTION_TEXT = "Text"; 
	public static final String FILE_DESCRIPTION_PUBLIC = "Public"; 

	private static final Log _log = LogFactory.getLog("org.hydra.utils.FileUtils");	
	public static final String generalImageFormat = "png";

	public static String saveImage4(String inAppId, BufferedImage inImage){
		// 0. Generate pathname for new image
		String uri4Image = Utils.F("files/%s/files/%s.%s", inAppId, RandomStringUtils.random(8,true,true), generalImageFormat);
		// 1. Save image in PNG formate
		File output = getRealFile(uri4Image);
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
			String ifFileNameEndWith){
		getListOfFiles4Dir(URL, result, ifFileNameEndWith, true);
	}	
	public static void getListOfFiles4Dir(
			String URL,
			List<String> result,
			String ifFileNameEndWith,
			boolean includeSubDirs) {
					
		File dir = getRealFile(URL);
		if(dir != null && !dir.exists()){
			_log.error("Directory not exist: " + dir.getPath());
			return;
		}
		if(dir.isDirectory() && dir.list() != null){
			for(String path2File: dir.list()){
				File file = new File(dir.getPath(), path2File);
				if(file.isDirectory() && includeSubDirs){
					getListOfFiles4Dir(URL + path2File, result, ifFileNameEndWith);
				}else if(file.isFile()){
					if(ifFileNameEndWith == null || file.getName().endsWith(ifFileNameEndWith)){
							result.add(URL + path2File);
					}
				}
			}
		}
	}

	public static String saveFile(CommonMessage inMessage) {
		if(!AMessageHandler.validateFile(inMessage)){			
			return("ERROR: no file!");
		}		
		if(!AMessageHandler.validateData(inMessage, "appid", "folder", "file_path", "file_real_path")){
			return("ERROR: no valid parameters!");
		}
		String appId = inMessage.getData().get("appid");
		FileTransfer file = inMessage.getFile();		
		String realPath = inMessage.getData().get("file_real_path");

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
			resultStr = getFileBox(appId, inMessage.getData().get("file_path"));
		} catch (Exception e) {
			_log.error(e.toString());
			resultStr = e.toString();
		}	
		// finish
		return (resultStr);		
	}
	
	public static boolean saveFileAndDescriptions(CommonMessage inMessage,
			StringWrapper outFilePath, String ... dataDescriptionKeys) {
		FileTransfer file = inMessage.getFile();
		boolean result = false;
		// 0. Generate pathname for new image
		String orginalFileName = sanitize(file.getFilename());		
		String encodedFileName = getMD5FileName(orginalFileName) + getFileExtension(orginalFileName);
		String uri4File = getUri4File(inMessage, encodedFileName);
		String realPath = getRealPath(inMessage, encodedFileName);
		_log.debug("orginalFileName: " + orginalFileName);
		
		result = saveFile(realPath, file);
		result = saveFileDescriptions(inMessage, realPath, dataDescriptionKeys);
			
		if(result)
			outFilePath.setString(String.format("%s/%s", inMessage.getContextPath(), uri4File));
		
		return result;
	}
	
	public static String getUri4File(CommonMessage inMessage, String inFileName){
		return(Utils.F(URL4FILES_APPID_SUBFOLDER, inMessage.getData().get("appid"), inMessage.getData().get("folder")) + inFileName);
	}
	
	public static String getRealPath(CommonMessage inMessage, String inFileName){
		return(getRealFile(getUri4File(inMessage, inFileName)).getPath());
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
			String filePath, String ... dataDescriptionKeys) {
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
	
	public static boolean saveFile(String realPathFile, FileTransfer file) {
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
	
	public static String getFileBox(String inAppID, String inFilePath) {
		if(inFilePath == null){
			_log.warn("Trying to process null inFilePath");
			return("");
		}
		
		String fileExtension = getFileExtension(inFilePath);
		if(fileExtension == null){
			_log.warn("NULL file extension for: " + inFilePath);
			return("");
		}
		
		StringBuffer content = new StringBuffer();
    	String divHiddenID = Utils.sanitazeHtmlId("template_" + inFilePath);  
		content.append("<div style=\"margin: 5px; padding: 5px; border: 1px solid rgb(127, 157, 185);\">");
		
    	content.append(getDeleteLink("AdmFiles", Utils.Q(Constants._admin_app_action_div), inAppID, inFilePath) + " ");
    	content.append("[<strong>" + fileExtension + "</strong>] ");
    	
    	String htmlTag = "NOT_DEFINED";
		if(ImageUtils.validate(inFilePath)){
			htmlTag = Utils.F("<img src=\"%s\" border=\"0\">", inFilePath);
		}else{			
			htmlTag = Utils.F("<a href=\"%s\" target=\"_blank\">TEXT</a>", inFilePath);
		}
		content.append(Utils.toogleLink(divHiddenID, inFilePath));
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
				,"file_path", Utils.Q(key)
				,"dest", Utils.sanitazeHtmlId(inDest)
			);
		return(Utils.F("[%s]", Utils.createJSLinkWithConfirm("Delete",jsData, "X")));		
	}

	public static String getFilePropertiesDescription(IMessage inMessage, String propertiesFilePath) {
		boolean isAdmin = Roles.isUserHasRole(Roles.USER_ADMINISTRATOR, inMessage);
		Properties properties = parseProperties(propertiesFilePath);
		String Public = properties.getProperty(FILE_DESCRIPTION_PUBLIC);
    	boolean isPublic = ((Public != null) 
    			&& (Public.compareToIgnoreCase("true") == 0)) ? true : false;
		
    	String divHiddenID = Utils.sanitazeHtmlId("template." + sanitize(propertiesFilePath));
    	String Description = properties.getProperty(FILE_DESCRIPTION_TEXT);
    	if(isPublic || isAdmin){
			StringBuffer content = new StringBuffer();
			content.append("<div class=\"file_row\">");
			
			//TODO create delete link later
	    	// if(isAdmin)
	    	//	content.append(getDeleteLink("UserFiles", Utils.Q(Constants._user_content), inAppID, propertiesFilePath) + " ");
			
			// name
			content.append("<span class=\"file_name\">" + properties.get("Name") + "</span>");
			content.append("<br />");
	    	// download link
			String htmlTag = Utils.F("&nbsp;&nbsp;<a href=\"%s\" target=\"_blank\">%s</a>", 
					stripPropertiesExtension(propertiesFilePath),
					"[[Dictonary|Text|Download|NULL]]");
			content.append(htmlTag);
			content.append(" ");
			// description
			if(Description != null){ 
				content.append(Utils.toogleLink(divHiddenID, "[[Dictonary|Text|Description|NULL]]"));
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
		Properties properties = APropertyLoader.parsePropertyFile(getRealFile(propertiesFilePath));
		return properties;
	}

	public static String getHtmlFromFile(String inAppId, String fileName)  {
		String filePath = String.format("/files/%s/html/%s.html", inAppId, fileName);
		StringBuffer content = new StringBuffer(String.format("<!-- %s -->", fileName));
		
		File file = getRealFile(filePath);
		if(file == null || (!file.exists())){
			content.append(String.format("<!-- %s not found! -->", filePath));
			return(content.toString());
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis,Constants._utf_8));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()){
					content.append(line);
				}
			}
		} catch (IOException e) {
			content.append(String.format("<!-- ERROR: %s -->", e.getMessage()));
		}			
		return(content.toString()); 
	}
	
	public static File getRealFile(String inPath){
		if(WEBAPP_ROOT == null) return(null);
		return(new File(WEBAPP_ROOT, inPath));
	}	
	
	public static boolean isExistAppHtmlFile(String inAppId, String inFileName){
		String filePath = String.format("/files/%s/html/%s.html", inAppId, inFileName);
		File file = getRealFile(filePath);
		return(file != null && file.exists());
	}	
	
	public static String WEBAPP_ROOT = null;	
	
	public static boolean isImage(String filePath) {
		if(filePath == null || filePath.isEmpty()) return false;
		if(filePath.toUpperCase().endsWith(".BMP")
				|| filePath.toUpperCase().endsWith(".JPG")
				|| filePath.toUpperCase().endsWith(".GIF")
				|| filePath.toUpperCase().endsWith(".TIFF")
				|| filePath.toUpperCase().endsWith(".PNG")
				){
			return(true);
		}
		return(false);
	}

	public static String findLines(String inRealPath, String inSeekString, int inLimit) {
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		String[] tokens = inSeekString.split("\\s+");
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(inRealPath)));
			String next_line;
			int lines = 0; int found_lines = 0;
			while((next_line = br.readLine()) != null){
				lines++;
				boolean all_tokens_found = true;
				for(String token:tokens){
					if(!next_line.toLowerCase().contains(token.toLowerCase())){
						all_tokens_found = false;
						break;
					}
				}
				if(all_tokens_found){
					found_lines ++;
					if(found_lines < inLimit){
						sb2.append("<small>" + next_line + "</small><br />");
					}
				}
			}
			sb.append("<h1>All (Founds): " + lines + " (" + found_lines + ") records</h1>");			
			sb.append("<h1>Limit: " + inLimit + "</h1>");
			if(sb2.length() > 0) sb.append(sb2);
		} catch (Exception e) {
			sb.append(e.getMessage());
			_log.error(e.getMessage());
		}
		return(sb.toString());
	}
}
