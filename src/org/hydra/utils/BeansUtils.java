package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public final class BeansUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.BeansUtils");
	private GenericApplicationContext ctx = null;
	private static XmlBeanDefinitionReader xmlReader = null;
	private static BeansUtils me = null;
	
	private BeansUtils(){
		_log.debug("Create new GenericApplicationContext!");
		ctx = new GenericApplicationContext();
		xmlReader = new XmlBeanDefinitionReader(ctx);
		xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));
		ctx.refresh();
	}
	
	private static BeansUtils Me(){
		if(me == null)
			me = new BeansUtils();
		return(me);
	}	

	public static Object getBean(String inName){
		_log.debug("Try to get bean: " + inName);
		if(Me().ctx == null){
			_log.fatal("GenericApplicationContext not defined!");
			return(null);
		}
		return (Me().ctx.getBean(inName));
	}
	
	public static void getWebContextBean(Result result, String inBeanId) {
//		try {
			_log.debug("Try to find bean: " + inBeanId);
			Object o = Me().ctx.getBean(inBeanId);
			if(o == null)
				_log.warn("Bean o is NULL!");
			else
				_log.debug("Bean o IS: " + o.getClass().getName());
			result.setObject(o);
			result.setResult(true);			
			/*
		} catch (Exception e) {
			_log.warn(e.getMessage());
			result.setErrorString(e.getMessage());
			result.setResult(false);
		}		
		*/
	}

}
