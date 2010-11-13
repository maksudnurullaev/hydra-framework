package org.hydra.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hydra.beans.abstracts.APropertyLoader;
import org.hydra.utils.Constants;

public class TextManager extends APropertyLoader {
	private static final String _fileMessagesBasename = "messages";
	private static final String _fileTemplateName = "templates";
	private Map<String, String> _locales = new HashMap<String, String>();
	private String defaultLocale;
	private Map<String, Properties> _dictionary = new HashMap<String, Properties>();
	
	public void setLocales(Map<String, String> _locales) {
		this._locales = _locales;
	}

	public Map<String, String> getLocales() {
		return _locales;
	}	
	
	public String getDefaultLocale() {
		return defaultLocale;
	}

	public TextManager() {
		super();
	}

	public String getBasename() {
		return _fileMessagesBasename;
	}
	
	public void setDefaultLocale(String inLocale){
		defaultLocale = inLocale;			
		getLog().debug("Set default locale to: " + inLocale);
	}

	public String getTemplate(String inKey){
		// Load templates file, if not loaded yet
		if(!_dictionary.containsKey(_fileTemplateName)){
			getLog().debug("Load templates first time...");
			_dictionary.put(_fileTemplateName, loadProperties(_fileTemplateName));
		}
		// If still not properly initialized!
		if(_dictionary.get(_fileTemplateName) == null){
			getLog().error("Templates is not properly loaded!");
			return "Templates is not properly loaded!";			
		}
		// If not found template by key
		if(!_dictionary.get(_fileTemplateName).containsKey(inKey))
			return "Could not find template by key = " + inKey;		
		
		return _dictionary.get(_fileTemplateName).getProperty(inKey);
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
		
		if(!_dictionary.get(inLocale).containsKey(inKey))
			return "Could not find text by key = " + inKey;
		
		if(inHtmlWrap == null)
			return _dictionary.get(inLocale).getProperty(inKey);	

		return String.format("<%s id='%s'>%s</%s>", inHtmlWrap, inKey, _dictionary.get(inLocale).getProperty(inKey), inHtmlWrap); 
	}
		
	private String getFileName(String inLocale) {
		return _fileMessagesBasename + Constants._file_name_delimiter + inLocale;
	}

}
