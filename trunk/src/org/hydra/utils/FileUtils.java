package org.hydra.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.lang.RandomStringUtils;

public final class FileUtils {
	public static final String generalImageFormat = "png";
	public static String saveImage4(ServletContext servletContext, String inAppId, BufferedImage inImage){
		// 0. Generate pathname for new image
		String uri4Image = Utils.F("img/%s/%s.%s", inAppId, RandomStringUtils.random(8,true, true), generalImageFormat);
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
}
