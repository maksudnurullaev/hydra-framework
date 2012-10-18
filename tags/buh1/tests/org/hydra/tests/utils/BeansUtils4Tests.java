package org.hydra.tests.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplications;
import org.hydra.utils.Constants;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public final class BeansUtils4Tests {
//	public final static Resource res = 
//		new FileSystemResource("WebContext/WEB-INF/applicationContext.xml");
	static Log _log = LogFactory.getLog("org.hydra.utils.BeansUtils4Tests");
	private GenericApplicationContext ctx = null;
	private static XmlBeanDefinitionReader xmlReader = null;
	private static BeansUtils4Tests me = null;
	
	private BeansUtils4Tests(){
		_log.debug("Create new GenericApplicationContext!");
		ctx = new GenericApplicationContext();
		xmlReader = new XmlBeanDefinitionReader(ctx);
		xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));
		ctx.refresh();		
	}
	
	private static BeansUtils4Tests Me(){
		if(me == null)
			me = new BeansUtils4Tests();
		return(me);
	}
	
	public static Object getBean(String inName){
		_log.debug("Try to get bean: " + inName);
		 return (Me().ctx.getBean(inName));
	}

	public static WebApplications getWebAppsMngr() {
		return (WebApplications) getBean(Constants._bean_hydra_web_applications);
	}	

}
