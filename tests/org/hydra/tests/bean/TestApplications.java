package org.hydra.tests.bean;


import org.hydra.beans.Applications;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestApplications extends Test0 {
	static Applications applicatons = null;
	
	@BeforeClass
	public static void test_0(){
		Object o = BeansUtils4Tests.getBean(Constants._beans_hydra_applications);
		Assert.assertTrue(o instanceof Applications);
		applicatons = (Applications) o;		
		Assert.assertNotNull(applicatons);
	}	
	@Test
	public void test_1(){
		Assert.assertTrue(applicatons.isValidUrl("http://127.0.0.1"));
		Assert.assertTrue(applicatons.isValidUrl("https://127.0.0.1"));
		Assert.assertTrue(applicatons.isValidUrl("http://www.hydra.uz"));
		Assert.assertTrue(applicatons.isValidUrl("https://www.hydra.uz"));
		Assert.assertTrue(applicatons.isValidUrl("http://hydra.uz"));
		Assert.assertTrue(applicatons.isValidUrl("https://hydra.uz"));
	}
}
