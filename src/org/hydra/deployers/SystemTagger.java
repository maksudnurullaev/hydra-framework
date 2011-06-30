package org.hydra.deployers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.utils.Utils;

public final class SystemTagger {

	public static String getKeyHow(
			String inKey, // elementId
			String inHow, // prefix
			String inLocale,
			String inApplicationID, 
			String inUserID) {
		
		List<String> tagPrefixes = new ArrayList<String>();
		tagPrefixes.add(inHow);
		
		return Utils.tagsAsEditableHtml(inApplicationID, inKey, "", null, null, tagPrefixes);
	}

}
