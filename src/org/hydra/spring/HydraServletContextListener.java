package org.hydra.spring;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hydra.utils.Utils;

public class HydraServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
        Utils.WEBAPP_ROOT = sce.getServletContext().getRealPath(".");
	}

}
