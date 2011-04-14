package org.hydra.beans;


import java.util.Set;

import org.hydra.utils.abstracts.ALogger;

public class WebApplications extends ALogger {
	private Set<WebApplication> _applications = null;
	
	public void setApplications(Set<WebApplication> inApplicationsSet){
		getLog().debug("Responsible applications size: " + inApplicationsSet.size());
		if(getLog().isDebugEnabled()) for(WebApplication app:inApplicationsSet) getLog().debug(app.getId());
		_applications = inApplicationsSet;
	}
	
	public WebApplication getValidApplication(String inUrl) {
		for(WebApplication webApplication:_applications)
			if(webApplication.isValidUrl(inUrl)) return webApplication;		
		return null;
	}
	
	public String getDescription() {
		if(_applications == null || _applications.size() == 0)
			return "No applications";
		String result = "<pre>";
		for(WebApplication app:_applications) result += app.getDescription();
		return result + "</pre>";
	}	
}
