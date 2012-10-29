package org.hydra.spring;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class HydraServletContextListener implements ServletContextListener {

	public static String ROOT_DIR = null;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
        ROOT_DIR = sce.getServletContext().getRealPath(".");
	}

}
