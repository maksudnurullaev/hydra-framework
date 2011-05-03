package org.hydra.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Moder {
	public static Pattern pattern4ModeTemplate = Pattern.compile("mode=(template)");
	public static Pattern pattern4ModeText = Pattern.compile("mode=(text)");
	public static Pattern pattern4Id = Pattern.compile("id=([a-zA-Z\\.]+)");
	public enum MODE{
		MODE_UKNOWN,
		MODE_TEMPLATE, 
		MODE_TEXT
	};
	public Moder(String inUri){
		if(inUri == null) return;
		
		Matcher matcher = pattern4ModeTemplate.matcher(inUri);
		if(matcher.find()) _mode = MODE.MODE_TEMPLATE; 
		matcher = pattern4ModeText.matcher(inUri);
		if(matcher.find()) _mode = MODE.MODE_TEXT;
		
		matcher = pattern4Id.matcher(inUri);
		if(matcher.find()) _id = matcher.group(1);
	};
	
	
	private MODE _mode = MODE.MODE_UKNOWN;
	private String _id = null;
	
	public void setMode(MODE _mode) {
		this._mode = _mode;
	}
	public MODE getMode() {
		return _mode;
	}
	public void setId(String _id) {
		this._id = _id;
	}
	public String getId() {
		return _id;
	}
	
}
