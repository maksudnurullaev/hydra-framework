package org.hydra.tests.utils;

import org.hydra.utils.Constants;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * To use this test we should create 1000 test users
 * 
 * @see Create100TestUsers
 * @author M.Nurullayev
 */
public final class Utils4Tests {
	final static Resource res = new FileSystemResource(Constants._path2ApplicationContext_xml);
	final static XmlBeanFactory factory = new XmlBeanFactory(res);

	
	public static BeanFactory getBeanFactory(){
		return factory;
	}
	
	public static Object getBean(String inName){
		return factory.getBean(inName);
	}
}
