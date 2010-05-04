package org.hydra.tests.db.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.Cf;
import org.hydra.db.beans.Ksp;
import org.hydra.tests.utils.Utils4Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestKSName {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = Utils4Tests.getBeanFactory();

	@Before
	public void test_main() {
		Assert.assertNotNull(factory);
	}
	
	@Test
	public void test_ksp_KSMain(){
		String bean_name = "_ksp_KSMain";
		Assert.assertTrue(factory.containsBean(bean_name));
		Assert.assertTrue(factory.getBean(bean_name) instanceof Ksp);		
	}	
}
