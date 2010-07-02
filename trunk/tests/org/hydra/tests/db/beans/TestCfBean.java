package org.hydra.tests.db.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.utils.BeansUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestCfBean {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = BeansUtils.getBeanFactory();

	@Before
	public void test_main() {
		Assert.assertNotNull(factory);
	}
	
	@Test
	public void test_type(){
		Assert.assertTrue(factory.containsBean("_cf_users"));
		Assert.assertTrue(factory.getBean("_cf_users") instanceof ColumnFamilyBean);		
	}	
}
