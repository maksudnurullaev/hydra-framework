package org.hydra.tests.beans;


import org.hydra.tests.Test0;
import org.junit.Assert;
import org.junit.Test;

public class TestBeanWebApplications extends Test0 {
	@Test
	public void test_1(){
		Assert.assertNotNull(_hydra_applicatons.getValidAppliction("http://127.0.0.1"));
		Assert.assertNotNull(_hydra_applicatons.getValidAppliction("https://127.0.0.1"));
		Assert.assertNotNull(_hydra_applicatons.getValidAppliction("http://www.hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidAppliction("https://www.hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidAppliction("http://hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidAppliction("https://hydra.uz"));
		Assert.assertNull(_hydra_applicatons.getValidAppliction("https://zero.hydra.uz"));
	}
}
