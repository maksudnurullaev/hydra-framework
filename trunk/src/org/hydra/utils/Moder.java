package org.hydra.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Moder {
	public static Pattern pattern4ModeEdit = Pattern.compile("mode=(edit)");
	//public static Pattern pattern4Id = Pattern.compile("id=([a-zA-Z\\.]+)");
	public enum MODE{
		MODE_UKNOWN,
		MODE_EDIT
	};
	public Moder(String inUri){
		if(inUri == null) return;
		
		Matcher matcher = pattern4ModeEdit.matcher(inUri);
		if(matcher.find()) _mode = MODE.MODE_EDIT; 
		
	};
	
	private MODE _mode = MODE.MODE_UKNOWN;
	
	public void setMode(MODE _mode) {
		this._mode = _mode;
	}
	public MODE getMode() {
		return _mode;
	}	
}
