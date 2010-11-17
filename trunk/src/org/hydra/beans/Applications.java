package org.hydra.beans;


import java.util.Set;
import org.hydra.utils.abstracts.ALogger;

public class Applications extends ALogger {
	private Set<Application> _applications = null;
	
	public void setApplications(Set<Application> inApplicationsSet){
		getLog().debug("Responsible applications size: " + inApplicationsSet.size());
		if(getLog().isDebugEnabled()) for(Application app:inApplicationsSet) getLog().debug(app.getName());
		_applications = inApplicationsSet;
	}
	public String getValidAppID4Url(String inUrl) {
		String result = null;
		for(Application app:_applications)
			if((result = app.findCorrespondingUrl(inUrl)) != null) return result;		
		return null;
	}
	public String getDescription() {
		if(_applications == null || _applications.size() == 0)
			return "No applications";
		String result = "<pre>";
		for(Application app:_applications) result += app.getDescription();
		return result + "</pre>";
	}
}
