package org.hydra.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hydra.text.abstracts.PropertyLoader;
import org.hydra.utils.Constants;

public class TextManager extends PropertyLoader {
	private String _fileBasename = "messages";
	private String _fileLocale = "ru";
	private Map<String, Properties> _dictionary = new HashMap<String, Properties>();
	
	public TextManager() {
		super();
	}
	
	public void setBasename(String name){
		_fileBasename = name;
		getLog().debug("Basename for property file is: " + _fileBasename);
		getLog().debug("Dictionary file name now: " + getFileName());
	}

	public String getBasename() {
		return _fileBasename;
	}

	public String getLocale() {
		return _fileLocale;
	}
	
	public void setDefaultLocale(String locale){
		_fileLocale = locale;
		getLog().debug("Set local to: " + _fileLocale);
		getLog().debug("Dictionary file name now: " + getFileName());
	}
	
	/**
	 * Return test wrapped as html div (by default) 
	 * @param inKey
	 * @return
	 */
	public String getTextByKey(String inKey){
		return getTextByKey(inKey, null, getLocale());
	}
	
	public String getTextByKey(String inKey, String inHtmlWrap){
		return getTextByKey(inKey, inHtmlWrap, getLocale());
	}	
	
	public String getTextByKey(String inKey, String inHtmlWrap, String inLocale) {
		// Load properties file, if not loaded yet
		if(!_dictionary.containsKey(inLocale)){ 
			_dictionary.put(inLocale, loadProperties(getFileName(inLocale)));
			getLog().debug(String.format("Propetry file %s has %d key(s)", getFileName(inLocale),_dictionary.get(inLocale).size()));			
		}
		
		// If still not properly initialized!
		if(_dictionary.get(inLocale) == null){
			getLog().error(String.format("Locale(%s) is not properly initiated!", inLocale));
			return "TextManager is not properly initiated!";
		}		
		
		if(!hasKey(inKey, inLocale))
			return "Could not find text by key = " + inKey;
		
		if(inHtmlWrap == null)
			return _dictionary.get(inLocale).getProperty(inKey);	

		return String.format("<%s id='%s'>%s</%s>", inHtmlWrap, inKey, _dictionary.get(inLocale).getProperty(inKey), inHtmlWrap); 
	}
	
	public boolean hasKey(String inKey){
		return hasKey(inKey, getLocale());
	}
	
	public boolean hasKey(String inKey, String inLocale) {
		return _dictionary.get(inLocale).containsKey(inKey);
	}	
	
	public String getFileName() {
		return _fileBasename + Constants._file_name_delimiter + _fileLocale;
	}
	
	public String getFileName(String inLocale) {
		return _fileBasename + Constants._file_name_delimiter + inLocale;
	}	
}
