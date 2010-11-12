package org.hydra.tests.db.beans;

import org.hydra.beans.db.ColumnFamilyBean;
import org.hydra.tests.bean.Test0;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.Test;

public class TestCf extends Test0 {
	@Test
	public void test_type(){
		Assert.assertTrue(BeansUtils4Tests.getBean("Users") != null);
		Assert.assertTrue(BeansUtils4Tests.getBean("Users") instanceof ColumnFamilyBean);		
	}	
}
