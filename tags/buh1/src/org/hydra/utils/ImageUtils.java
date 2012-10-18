package org.hydra.utils;

import java.util.Properties;
import java.util.regex.Pattern;

import org.hydra.beans.abstracts.APropertyLoader;
import org.hydra.messages.handlers.GalleryImages;

public class ImageUtils{
	 
	   private static final String IMAGE_PATTERN = 
	                "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
	 
	   private static Pattern pattern = Pattern.compile(IMAGE_PATTERN);
	 
	   /**
	   * Validate image with regular expression
	   * @param image image for validation
	   * @return true valid image, false invalid image
	   */
	   public static boolean validate(final String image){
		   if(image == null) return(false);
	 
		   return(pattern.matcher(image).matches());
	   }

	public static String getImageDescription(String filePath) {
		Properties properties = FileUtils.parseProperties(filePath + APropertyLoader.SUFFIX);
		String imageDescription = (properties != null && properties.containsKey(GalleryImages.Description)?properties.getProperty(GalleryImages.Description):"No description");
		return imageDescription;
	}
	}
