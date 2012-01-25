package org.hydra.tests.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplications;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public final class BeansUtils4Tests {
	public final static Resource res = 
		new FileSystemResource("WebContext/WEB-INF/applicationContext.xml");
	static Log _log = LogFactory.getLog("org.hydra.utils.Utils");
	
	public static Object getBean(String inName){
		return(null);
	}

	public static Result getWebSessionBean(String inBeanId){
		Result result = new Result();
		return result;
	}

	public static WebApplications getWebAppsMngr() {
		return (WebApplications) getBean(Constants._bean_hydra_web_applications);
	}	

}
