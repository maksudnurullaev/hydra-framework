package org.hydra.deployers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.Utils;

public final class Dictionary {
	private static final Log _log = LogFactory.getLog("org.hydra.deployers.Dictionary");
	static final String _file_name = "dictionary.properties";
	static String defaultLocale = "eng";
	private static Map<String, Properties> _values = null;
	private static Map<String, String> locales = null;

	public static void setLocales(Map<String, String> locales) {
		Dictionary.locales = locales;
	}

	public static String getDefaultLocale() {
		return defaultLocale;
	}

	public static void setDefaultLocale(String inDefaultLocale) {
		defaultLocale = inDefaultLocale;
	}

	public static void init_me(){
		ClassLoader loader = Dictionary.class.getClassLoader();
		InputStream in = null;

		try {
			in = loader.getResourceAsStream(_file_name);
			if (in != null) { parseIt(in); }
		} catch (Exception e) {
			_log.error(e.toString());
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
					_log.error(e.toString());
				}
		}
	}
	
	public static void parseIt(InputStream in)
			throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, Constants._utf_8));
	
		String line = null;
		String curKey = null;
		String curValue = null;
		int found = -1;
		while ((line = reader.readLine()) != null) {
			if (line.trim().isEmpty() || line.matches("^[#]+.*"))
				continue;
			
			if (!line.matches("^[\\s]+.*")) {
				found = line.indexOf("=");
				if (found != -1) {
					if(curKey != null)setProperty(curKey, curValue);
					curKey = line.substring(0, found).trim();
					curValue = line.substring(found + 1, line.length()).trim();
				} else if(line.charAt(3) == ':') {
					setProperty(line.trim(), ""); // null value
				}else{
					curValue += line;
				}
			} else {
				if(curValue != null)
					curValue += " " + line.trim(); // remove first tabs
				else
					curValue = " " + line.trim();
			}
		}
		// Save last pair
		setProperty(curKey, curValue);
	}
	
	private static void setProperty(String inKey,
			String inValue) {
		int found = inKey.indexOf(":");
		if(found == -1){ return; }
		String locale = inKey.substring(0, found).trim().toLowerCase();
		String key = inKey.substring(found + 1, inKey.length()).trim();
		if(!_values.containsKey(locale)){
			_values.put(locale, new Properties());
		}
		_values.get(locale).setProperty(key, inValue);
	}	

	public static String getTextByKey(
			String inKey, 
			String inDefaultValue,
			String inLocale) {
		if(_values == null){
			_values = new HashMap<String, Properties>();
			init_me(); 
		}
		inLocale = inLocale.toLowerCase(); // always!
		if(inKey != null && inLocale != null && _values != null && _values.containsKey(inLocale)){
			if(_values.get(inLocale).containsKey(inKey)){
				String value = _values.get(inLocale).getProperty(inKey);
				if(value != null && !value.isEmpty()){
					return(value);
				} else if(inDefaultValue != null) {
					return(inDefaultValue);
				} else {	
					return(inKey);
				}
			}
		}
		return("(" + inKey + ")");
	}

	public static Map<String, String> getLocales() {
		return(locales);
	}

	public static String getTextByKey(
			String inKey, 
			String inHow,
			IMessage inMessage) {
		String locale = Utils.getMessageDataOrNull(inMessage, Constants._locale_key);
		if(locale == null){ return ("(" + inKey + ")"); }
		return(getTextByKey(inKey, inHow, locale));
	}
}
