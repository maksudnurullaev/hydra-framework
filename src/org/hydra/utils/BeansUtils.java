package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public final class BeansUtils {
	public final static Resource res = new FileSystemResource(Constants._path2ApplicationContext_xml);
	public static XmlBeanFactory factory = new XmlBeanFactory(res);
	static Log _log = LogFactory.getLog("org.hydra.utils.Utils");
	
	public static Object getBean(String inName){
		return BeansUtils.factory.getBean(inName);
	}

	public static BeanFactory getBeanFactory(){
		return BeansUtils.factory;
	}


	public static WebApplicationContext getWebApplicationContext() {
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

	public static CassandraDescriptorBean getCassandraDescriptor() {
		Result result = getWebSessionBean(Constants._beans_cassandra_descriptor);
		
		if(result.isOk() && result.getObject() instanceof CassandraDescriptorBean){
			_log.debug("Found bean: " + Constants._beans_cassandra_descriptor);
			return (CassandraDescriptorBean) result.getObject();
		}
		_log.fatal("Could not find bean: " + Constants._beans_cassandra_descriptor);
		return null;
	}

	public static CassandraAccessorBean getCassandraAccessor() {
		Result result = getWebSessionBean(Constants._beans_cassandra_accessor);
		
		if(result.isOk() && result.getObject() instanceof CassandraAccessorBean){
			_log.debug("Found bean: " + Constants._beans_cassandra_accessor);
			return (CassandraAccessorBean) result.getObject();
		}
		_log.fatal("Could not find bean: " + Constants._beans_cassandra_accessor);
		return null;
	}

}
