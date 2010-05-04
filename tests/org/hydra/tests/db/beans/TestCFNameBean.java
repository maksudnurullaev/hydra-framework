package org.hydra.tests.db.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.Cf;
import org.hydra.tests.utils.Utils4Tests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestCFNameBean {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = Utils4Tests.getBeanFactory();

	@Before
	public void test_main() {
		Assert.assertNotNull(factory);
	}
	
	@Test
	public void test_type(){
		Assert.assertTrue(factory.containsBean("_cf_article"));
		Assert.assertTrue(factory.getBean("_cf_article") instanceof Cf);		
	}	
}
