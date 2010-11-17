package org.hydra.beans;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.hydra.utils.abstracts.ALogger;

public class Application extends ALogger{
	private Set<Pattern> _urlPatterns = new HashSet<Pattern>();
	private Application _parentApplication = null;
	private String _name;	
	private Set<String> _stylesheets = null;
	
	public void setStylesheets(Set<String> stylessheets) {
		this._stylesheets = stylessheets;
	}
	public Set<String> getStylesheets() {
		return _stylesheets;
	}
	public void setUrls(Set<String> inUrlsSet){
		getLog().debug("Responsible urls size: " + inUrlsSet.size());
		if(getLog().isDebugEnabled()) for(String url:inUrlsSet) getLog().debug(url);
		for(String ulrPattern:inUrlsSet) _urlPatterns.add(Pattern.compile(ulrPattern));
	} 
	public String getName() {
		return _name;
	}
	public void setName(String inName){
		_name = inName;
	}
	
	public void setParentApplication(Application inAparentApplication) {
		this._parentApplication = inAparentApplication;
	}
	public Application getParentApplication() {
		return _parentApplication;
	}
	public String findCorrespondingUrl(String inUrl) {
		for(Pattern p:_urlPatterns) if(p.matcher(inUrl).matches()) return _name;
		return null;
	}
	public String getDescription() {
		String result = _name + "\n";
		for(Pattern p:_urlPatterns) result += "\t" + p.pattern() + "\n";
		return result;
	}
}
