package org.hydra.beans;


import java.util.HashSet;
import java.util.Set;

import org.hydra.utils.abstracts.ALogger;

public class WebApplications extends ALogger {
	private Set<WebApplication> _applications = new HashSet<WebApplication>();
	private KspManager _kspManager;
	private static String _main_application_id = "";
	
	public WebApplications(KspManager inKspManager){
		this._kspManager = inKspManager;
	}
	
	public void setApplications(Set<WebApplication> inApplicationsSet){
		getLog().debug("Responsible applications size: " + inApplicationsSet.size());
		_applications = inApplicationsSet;
		
		getLog().debug("Initiate DB for applications");
		initDb(_applications);
	}
	
	public void setMainApplication(WebApplication inApp){
		getLog().debug("Set main application: " + inApp.getId());
		_main_application_id = inApp.getId();
	}
	
	public static boolean isMainApplication(WebApplication inApp){
		return(_main_application_id.compareToIgnoreCase(inApp.getId()) == 0);
	}
	
	private void initDb(Set<WebApplication> inApplications) {
		getLog().debug("Initialize databases");
		for(WebApplication app:inApplications){
			initDb(app.getId());
		}
	}
	
	private void initDb(String inAppId) {
		getLog().debug("Initialize database for: " + inAppId);
		_kspManager.initApp(inAppId);
	}

	public WebApplication getValidApplication4(String inUrl) {
		for(WebApplication webApplication:_applications)
			if(webApplication.isValidUrl(inUrl)) return webApplication;		
		return null;
	}
	
	public Set<WebApplication> getApplications(){
		return _applications;
	}
	
	public String getDescription() {
		if(_applications == null || _applications.size() == 0)
			return "No applications";
		String result = "<pre>";
		for(WebApplication app:_applications) result += app.getDescription();
		return result + "</pre>";
	}

	public void setKspManager(KspManager kspManager) {
		this._kspManager = kspManager;
	}
}
