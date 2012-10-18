package org.hydra.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.abstracts.ALogger;
import org.springframework.context.ApplicationContext;

/**
 * This class provides application-wide access to the Spring ApplicationContext.
 * The ApplicationContext is injected by the class "ApplicationContextProvider".
 *
 * @author Siegfried Bolz
 */
public class AppContext extends ALogger{

	private static Log _log = LogFactory.getLog("org.hydra.spring.AppContext");	
	
    private static boolean debugMode = false;

    /**
     * Injected from the class "ApplicationContextProvider" which is automatically
     * loaded during Spring-Initialization.
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
    	_log.info("Spring AppContext initialized successfully");
    }

    
    
    public static boolean isDebugMode(){
    	_log.debug("AppContext: get debug mode: " + debugMode);
    	return debugMode;
    }
    
    public static void setDebugMode(boolean inDebugMode){
    	debugMode = inDebugMode;
    }
} // .EOF
