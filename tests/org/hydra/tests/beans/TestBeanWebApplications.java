package org.hydra.tests.beans;


import org.hydra.tests.BaseTest;
import org.junit.Assert;
import org.junit.Test;

public class TestBeanWebApplications extends BaseTest {
	@Test
	public void test_1(){
		Assert.assertNotNull(_hydra_applicatons.getValidApplication4("http://127.0.0.1"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication4("https://127.0.0.1"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication4("http://www.hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication4("https://www.hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication4("http://hydra.uz"));
		Assert.assertNotNull(_hydra_applicatons.getValidApplication4("https://hydra.uz"));
		Assert.assertNull(_hydra_applicatons.getValidApplication4("https://zero.hydra.uz"));
	}
}
