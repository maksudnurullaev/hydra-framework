package org.hydra.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.hydra.utils.abstracts.ALogger;

public class WebApplication extends ALogger {
	private String _id;
	private Set<Pattern> _urlPatterns = new HashSet<Pattern>();
	
	private Map<String, String> _locales = new HashMap<String, String>();	
	private String _defaultLocale = null;
	private String _jscript = null;
	private String _styleSheet = null;
	private String _title = "...";
	
	public String getId() {
		return _id;
	}

	public void setId(String inId) {
		this._id = inId;
	}

	public void setStyleSheet(String stylessheet) {
		this._styleSheet = stylessheet;
	}

	public String getStyleSheet() {
		return _styleSheet;
	}

	public void setUrls(Set<String> inUrlsSet) {
		getLog().debug("Responsible urls size: " + inUrlsSet.size());
		if (getLog().isDebugEnabled())
			for (String url : inUrlsSet)
				getLog().debug(url);
		for (String ulrPattern : inUrlsSet)
			_urlPatterns.add(Pattern.compile(ulrPattern));
	}

	protected boolean isValidUrl(String inUrl) {
		for (Pattern p : _urlPatterns)
			if (p.matcher(inUrl).matches())
				return true;
		return false;
	}

	public String getDescription() {
		String result = _id + "\n";
		for (Pattern p : _urlPatterns)
			result += "\t" + p.pattern() + "\n";
		return result;
	}

	public void setLocales(Map<String, String> _locales) {
		this._locales = _locales;
	}

	public Map<String, String> getLocales() {
		return _locales;
	}

	public void setDefaultLocale(String defaultLocale) {
		this._defaultLocale = defaultLocale;
	}

	public String getDefaultLocale() {
		return _defaultLocale;
	}

	public void setJscript(String jscript) {
		this._jscript = jscript;
	}

	public String getJscript() {
		return _jscript;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	public String getTitle() {
		return _title;
	}

}
