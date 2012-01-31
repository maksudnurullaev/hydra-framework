package org.hydra.deployers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.messages.CommonMessage;
import org.hydra.utils.Utils;

public final class SystemTagger {

	public static String getKeyHow(
			String inKey, // elementId
			String inHow, // prefix
			CommonMessage inMessage) {
		
		List<String> tagPrefixes = new ArrayList<String>();
		tagPrefixes.add(inHow);
		
		return Utils.tagsAsEditableHtml(inMessage.getData().get("_appid"), inKey, "", null, null, tagPrefixes);
	}

}
