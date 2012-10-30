package org.hydra.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.io.FileTransfer;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.HydraServletContextListener;

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
		if(!AMessageHandler.validateData(inMessage, Constants._appid_key, Constants._folder, Constants._file_path, Constants._file_real_path)){
			return("ERROR: no valid parameters!");
		}
		FileTransfer file = inMessage.getFile();		
		String realPath = Utils.getMessageDataOrNull(inMessage, Constants._file_real_path);

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
			
		if(result)
			outFilePath.setString(String.format("%s/%s", inMessage.getContextPath(), uri4File));
		
		return result;
	}
	
	public static String getUri4File(CommonMessage inMessage, String inFileName){
		return(Utils.F(URL4FILES_APPID_SUBFOLDER, Utils.getMessageDataOrNull(inMessage, Constants._appid_key), Utils.getMessageDataOrNull(inMessage, Constants._folder)) + inFileName);
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
		if(HydraServletContextListener.ROOT_DIR == null) return(null);
		return(new File(HydraServletContextListener.ROOT_DIR, inPath));
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

	public static void setupFile(IMessage inMessage) {
		_log.debug("File name/size: " + inMessage.getFile().getFilename() + "/" + inMessage.getFile().getSize());
		String appId = Utils.getMessageDataOrNull(inMessage, Constants._appid_key);
		String folder = Utils.getMessageDataOrNull(inMessage, Constants._folder);
		String uri4File = Utils.F(FileUtils.URL4FILES_APPID_SUBFOLDER, appId, folder) + FileUtils.sanitize(inMessage.getFile().getFilename());
		inMessage.getData().put(Constants._file_path, uri4File);
		_log.debug("File uri: " + uri4File);
		inMessage.getData().put(Constants._file_real_path, FileUtils.getRealFile(uri4File).getPath());
		_log.debug("Real path: " + Utils.getMessageDataOrNull(inMessage, Constants._file_real_path));	
	}
}
