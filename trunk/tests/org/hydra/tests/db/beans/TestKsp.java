package org.hydra.tests.db.beans;

import org.hydra.beans.db.KeyspaceBean;
import org.hydra.tests.bean.Test0;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.Test;

public class TestKsp extends Test0{
	@Test
	public void test_ksp_KSMain(){
		String bean_name = "HydraUz";
		Assert.assertTrue(BeansUtils4Tests.getBean(bean_name) != null);
		Assert.assertTrue(BeansUtils4Tests.getBean(bean_name) instanceof KeyspaceBean);		
	}	
}
