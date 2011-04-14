package org.hydra.tests.beans;


import org.hydra.tests.BaseTest;
import org.junit.Assert;
import org.junit.Test;

public class TestBeanWebApplications extends BaseTest {
	@Test
	public void test_1(){
		Assert.assertNotNull(_hydra_applicatons.getValidApplication("http://127.0.0.1"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication("https://127.0.0.1"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication("http://www.hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication("https://www.hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication("http://hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication("https://hydra.uz"));
		Assert.assertNull(_hydra_applicatons.getValidApplication("https://zero.hydra.uz"));
	}
}
