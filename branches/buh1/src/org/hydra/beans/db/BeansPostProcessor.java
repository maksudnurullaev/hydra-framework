package org.hydra.beans.db;

import org.hydra.utils.abstracts.ALogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BeansPostProcessor extends ALogger implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object beanObject, String beanName)
			throws BeansException {
		getLog().debug(String.format("Bean(%s) initilazed as %s", beanName, beanObject.getClass().getSimpleName()));
		/* 
		if(beanObject instanceof ???)
			((???)beanObject).setup();
			*/
		return beanObject;
	}

	@Override
	public Object postProcessBeforeInitialization(Object beanObject, String beanName)
			throws BeansException {
		// Just return initial object
		return beanObject;
	}

}
