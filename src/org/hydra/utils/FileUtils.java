package org.hydra.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;

public final class FileUtils {
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
		for(String filepath: result){
			System.out.println("filepath: " + filepath);			
		}
		
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
}
