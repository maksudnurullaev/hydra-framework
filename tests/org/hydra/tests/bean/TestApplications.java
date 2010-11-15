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
		Assert.assertNotNull(applicatons.getValidAppID4Url("http://127.0.0.1"));
		Assert.assertNotNull(applicatons.getValidAppID4Url("https://127.0.0.1"));
		Assert.assertNotNull(applicatons.getValidAppID4Url("http://www.hydra.uz"));
		Assert.assertNotNull(applicatons.getValidAppID4Url("https://www.hydra.uz"));
		Assert.assertNotNull(applicatons.getValidAppID4Url("http://hydra.uz"));
		Assert.assertNotNull(applicatons.getValidAppID4Url("https://hydra.uz"));
		Assert.assertNull(applicatons.getValidAppID4Url("https://zero.hydra.uz"));
	}
}
