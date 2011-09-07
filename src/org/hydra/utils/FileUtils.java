package org.hydra.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.io.FileTransfer;

public final class FileUtils {
	public static final int FILE_TYPE_UNKNOWN = 0;
	public static final int FILE_TYPE_IMAGE = 1;
	public static final int FILE_TYPE_COMPRESSED = FILE_TYPE_IMAGE << 1;

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
	
	public static List<String> getListOfFiles(String inAppID) {
		List<String> result = new ArrayList<String>();
		String url = Utils.F("files/%s/image/", inAppID);
		
		getListOfFiles4Dir(url, result);

		return result;
		
	}
	private static void getListOfFiles4Dir(
			String URL,
			List<String> result) {
		
		if(!URL.endsWith("/")) URL += "/";
		
		String realURI = Utils.getServletContent().getRealPath(URL);

		File dir = new File(realURI);
		if(dir.isDirectory() && dir.list() != null){
			for(String path2File: dir.list()){
				File file = new File(realURI, path2File);
				if(file.isDirectory()){
					getListOfFiles4Dir(URL + path2File, result);
				}else if(file.isFile()){
					result.add(URL + path2File);
				}
			}
		}
	}

	public static String saveFile4Admin(
			ServletContext servletContext, 
			String inAppId,
			FileTransfer file) {
		// 0. Generate pathname for new image
		String uri4File = Utils.F("files/%s/image/%s", inAppId, file.getFilename());
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

	public static boolean saveTempFile(
			ServletContext servletContext, 
			String inAppId,
			FileTransfer file,
			StringWrapper outFilePath) {
		// 0. Generate pathname for new image
		String uri4File = Utils.F("files/%s/image/temp/%s", inAppId, file.getFilename());
		String realPath = servletContext.getRealPath(uri4File);
		String servletPath = servletContext.getContextPath();
		boolean result = false;
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
			outFilePath.setString(String.format("%s/%s", servletPath, uri4File));
			result = true;
		} catch (Exception e) {
			_log.error(e.toString());
			outFilePath.setString(e.toString());
			result = false;
		}	
		// finish
		return result;
	}	
	
	public static boolean saveTempFileAndTry2Zip(
			ServletContext servletContext, 
			String inAppId,
			FileTransfer file,
			StringWrapper outFilePath) {
		// initial variables
		int file_type = getFileType(file.getMimeType());
		boolean result = false;
		// 0. Generate pathname for new image
		String uri4FilePath;
		if((file_type & FILE_TYPE_COMPRESSED) > 0){
			uri4FilePath = Utils.F("files/%s/image/temp/%s", inAppId, file.getFilename());
			result = saveFile(servletContext.getRealPath(uri4FilePath), file);
		} else {
			uri4FilePath = Utils.F("files/%s/image/temp/%s.zip", inAppId, file.getFilename());
			result = saveFileAndZip(servletContext.getRealPath(uri4FilePath), file.getFilename(), file);
		}
		if(result)
			outFilePath.setString(String.format("%s/%s", servletContext.getContextPath(), uri4FilePath));
		
		return result;
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

	private static boolean saveFileAndZip(String realPathFile, String fileName, FileTransfer file) {
		InputStream is = null;
		FileOutputStream os = null;
		ZipOutputStream zos = null;
		byte[] bufer = new byte[4096];
		int bytesRead = 0;
		try {
			is = file.getInputStream();
			os = new FileOutputStream(realPathFile);			
			zos = new ZipOutputStream(os);
			zos.setLevel(9);
			ZipEntry ze = new ZipEntry(fileName);
			zos.putNextEntry(ze);
			while((bytesRead = is.read(bufer)) != -1){
				_log.debug("bytesRead: " + bytesRead);
				zos.write(bufer, 0, bytesRead);
			}
			zos.close();
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
		
    	content.append(getDeleteLink(inAppID, filePath) + " ");
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
			String inAppID, 
			String key) {
		String jsData = Utils.jsData(
				 "handler", Utils.Q("AdmFiles")
				,"action",  Utils.Q("delete")
				,"appid", Utils.Q(inAppID)
				,"key", Utils.Q(key)
				,"dest", Utils.Q("admin.app.action")
			);
		return(Utils.F("[%s]", Utils.createJSLinkWithConfirm("Delete",jsData, "X")));		
	}

	public static int getFileType(String mimeType) {
		int result = FILE_TYPE_UNKNOWN;
		if(mimeType.toUpperCase().contains("ZIP") 
			|| mimeType.toUpperCase().contains("COMPRESSED")
			|| mimeType.toUpperCase().contains("JPEG")
			|| mimeType.toUpperCase().contains("PNG")
			|| mimeType.toUpperCase().contains("GIF")
			|| mimeType.toUpperCase().contains("PDF")
		)
			result |= FILE_TYPE_COMPRESSED;
		
		if(mimeType.toUpperCase().contains("ZIP") 
				|| mimeType.toUpperCase().contains("COMPRESSED")
				|| mimeType.toUpperCase().contains("JPEG")
				|| mimeType.toUpperCase().contains("PDF")
			)
				result |= FILE_TYPE_IMAGE;
		return result;		
	}
}
