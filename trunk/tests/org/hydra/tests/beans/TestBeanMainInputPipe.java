package org.hydra.tests.beans;


import org.hydra.pipes.interfaces.IPipe;
import org.hydra.tests.Test0;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Test;

public class TestBeanMainInputPipe extends Test0 {	
	@Test
	public void test_mainPipe(){
		Assert.assertNotNull(BeansUtils4Tests.getBean(Constants._beans_main_input_pipe));
		Assert.assertTrue(BeansUtils4Tests.getBean(Constants._beans_main_input_pipe) instanceof IPipe);
	}	
}
