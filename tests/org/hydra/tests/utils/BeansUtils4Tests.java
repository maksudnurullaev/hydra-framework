package org.hydra.tests.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public final class BeansUtils4Tests {
	public final static Resource res = 
		new FileSystemResource("WebContext/WEB-INF/applicationContext.xml");
	public static XmlBeanFactory factory = new XmlBeanFactory(res);
	static Log _log = LogFactory.getLog("org.hydra.utils.Utils");
	
	public static Object getBean(String inName){
		return factory.getBean(inName);
	}

	public static Result getWebSessionBean(String inBeanId){
		Result result = new Result();
		try{
			result.setObject(factory.getBean(inBeanId));
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
