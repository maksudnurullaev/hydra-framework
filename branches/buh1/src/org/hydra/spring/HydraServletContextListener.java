package org.hydra.spring;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hydra.utils.FileUtils;

public class HydraServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
        FileUtils.WEBAPP_ROOT = sce.getServletContext().getRealPath(".");
	}

}
