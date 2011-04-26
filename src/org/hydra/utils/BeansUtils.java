package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public final class BeansUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.BeansUtils");

	private static Result getWebApplicationContext(Result result) {
		WebApplicationContext webApplicationContext =
				ContextLoader.getCurrentWebApplicationContext();
		if (webApplicationContext == null) {
			result.setResult("Could not get WebApplicationContext");
			_log.warn("Could not get WebApplicationContext");
			result.setResult(false);
		} else {
			result.setObject(webApplicationContext);
			result.setResult(true);
		}
		return result;
	}

	public static void getWebContextBean(Result result, String inBeanId) {
		getWebApplicationContext(result);
		if(!result.isOk() || !(result.getObject() instanceof WebApplicationContext)){
			result.setResult(false);
			result.setResult("Could not find WebApplicationContext instance!");
			_log.error("Could not find WebApplicationContext instance!");
			return ;
		}
		
		WebApplicationContext webApplicationContext =
			(WebApplicationContext) result.getObject();
		try {
			_log.debug("Try to find bean: " + inBeanId);
			Object o = webApplicationContext.getBean(inBeanId);
			if(o == null)
				_log.warn("Bean o is NULL!");
			else
				_log.debug("Bean o IS: " + o.getClass().getName());
			result.setObject(webApplicationContext.getBean(inBeanId));
			result.setResult(true);			
		} catch (Exception e) {
			_log.warn(e.getMessage());
			result.setResult(e.getMessage());
			result.setResult(false);
		}		
	}

}
