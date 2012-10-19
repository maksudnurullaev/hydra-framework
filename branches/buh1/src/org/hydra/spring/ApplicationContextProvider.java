package org.hydra.spring;

import org.hydra.utils.abstracts.ALogger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class provides an application-wide access to the
 * Spring ApplicationContext! The ApplicationContext is
 * injected in a static method of the class "AppContext".
 *
 * Use AppContext.getApplicationContext() to get access
 * to all Spring Beans.
 *
 * @author Siegfried Bolz
 */
public class ApplicationContextProvider extends ALogger implements ApplicationContextAware {

    private static boolean debugMode = false;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // Wiring the ApplicationContext into a static method
        AppContext.setApplicationContext(ctx);
        AppContext.setDebugMode(isDebugMode());
    }
    
    public boolean isDebugMode(){
    	getLog().debug("ApplicationContextProvider: get debug mode: " + debugMode);
    	return debugMode ;
    }
    
    public void setDebugMode(boolean inDebugMode){
    	getLog().debug("ApplicationContextProvider: set debug mode: " + debugMode);
    	debugMode = inDebugMode;
    }    
} // .EOF