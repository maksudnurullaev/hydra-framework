package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public final class BeansUtils {	
	private static Log _log = LogFactory.getLog("org.hydra.utils.BeansUtils");
	
	protected static Object getBean(String inName){
		return getWebApplicationContext().getBean(inName);
	}

	protected static WebApplicationContext getWebApplicationContext() {
		_log.debug("ContextLoader.getCurrentWebApplicationContext() is not null: " 
				+ (ContextLoader.getCurrentWebApplicationContext() != null));
		return ContextLoader.getCurrentWebApplicationContext();
	}

	public static Result getWebSessionBean(String inBeanId){
		Result result = new Result();
		try{
			result.setObject(getWebApplicationContext().getBean(inBeanId));
			result.setResult(true);
		}catch(Exception e){
			result.setResult(e.getMessage());
		}
		
		return result;
	}
	
	public static CassandraAccessorBean getAccessor() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) getBean(Constants._beans_cassandra_accessor);
		if(!accessor.isValid()) accessor.setup();
		return accessor;
	}

	public static CassandraDescriptorBean getDescriptor() {
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) getBean(Constants._beans_cassandra_descriptor);
		return descriptor;
	}	

}
