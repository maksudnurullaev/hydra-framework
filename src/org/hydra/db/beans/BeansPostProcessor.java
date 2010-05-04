package org.hydra.db.beans;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.utils.abstracts.ALogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BeansPostProcessor extends ALogger implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object beanObject, String beanName)
			throws BeansException {
		getLog().debug(String.format("Bean(%s) initilazed as %s", beanName, beanObject.getClass().getSimpleName()));
		// 1. For cassandra bean post initialization
		if(beanObject instanceof CassandraAccessorBean)
			((CassandraAccessorBean)beanObject).setup();
		return beanObject;
	}

	@Override
	public Object postProcessBeforeInitialization(Object beanObject, String beanName)
			throws BeansException {
		// Just return initial object
		return beanObject;
	}

}
