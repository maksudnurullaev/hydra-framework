package org.hydra.tests.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.pipes.interfaces.IPipe;
import org.hydra.utils.Constants;
import org.hydra.utils.BeansUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestSpringContext {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = BeansUtils.getBeanFactory();

	@Before
	public void test_main() {
		Assert.assertNotNull(factory);
	}
	
	@Test
	public void test_mainPipe(){
		Assert.assertTrue(factory.containsBean(Constants._beans_main_input_pipe));
		Assert.assertTrue(factory.getBean(Constants._beans_main_input_pipe) instanceof IPipe);
	}	
}
