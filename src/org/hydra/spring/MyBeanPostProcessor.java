package org.hydra.spring;

import org.hydra.db.server.CassandraBean;
import org.hydra.utils.abstracts.ALogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor extends ALogger implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object beanObject, String beanName)
			throws BeansException {
		getLog().debug(String.format("Bean(%s) initilazed as %s", beanName, beanObject.getClass().getSimpleName()));
		// 1. For cassandra bean post initialization
		if(beanObject instanceof CassandraBean)
			((CassandraBean)beanObject).setupServer();
		return beanObject;
	}

	@Override
	public Object postProcessBeforeInitialization(Object beanObject, String beanName)
			throws BeansException {
		// Just return initial object
		return beanObject;
	}

}
