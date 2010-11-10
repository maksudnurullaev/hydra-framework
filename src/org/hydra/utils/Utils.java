package org.hydra.utils;


/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class Utils {

	public static String wrap2HTMLTag(String inHTMLTagName, String inContent) {
		return String.format("<%s>%s</%s>", inHTMLTagName, inContent, inHTMLTagName);
	}
}
