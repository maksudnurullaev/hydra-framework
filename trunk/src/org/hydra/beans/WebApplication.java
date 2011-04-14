package org.hydra.beans;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.hydra.utils.abstracts.ALogger;

public class WebApplication extends ALogger {
	private String _id;
	private Set<Pattern> _urlPatterns = new HashSet<Pattern>();
	private Set<String> _stylesheets = null;

	public String getId() {
		return _id;
	}

	public void setId(String inId) {
		this._id = inId;
	}

	public void setStylesheets(Set<String> stylessheets) {
		this._stylesheets = stylessheets;
	}

	public Set<String> getStylesheets() {
		return _stylesheets;
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

}
