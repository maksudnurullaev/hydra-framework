package org.hydra.beans;

import java.util.Set;

import org.hydra.utils.abstracts.ALogger;

public class Applications extends ALogger {
	Set<Application> _applications = null;
	
	public void setApplications(Set<Application> inApplicationsSet){
		getLog().debug("Responsible applications size: " + inApplicationsSet.size());
		if(getLog().isDebugEnabled()) for(Application app:inApplicationsSet) getLog().debug(app.getName());
		_applications = inApplicationsSet;
	}
	public boolean isValidUrl(String inUrl) {
		for(Application app:_applications)
			if(app.findCorrespondingUrl(inUrl)) return true;
		
		return false;
	}
}
