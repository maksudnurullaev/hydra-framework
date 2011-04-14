package org.hydra.tests;

import org.hydra.beans.WebApplications;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.BeforeClass;

public class BaseTest {
	protected static WebApplications _hydra_applicatons = BeansUtils4Tests.getWebAppsMngr();
	
	@BeforeClass
	public static void test_0(){		
		Assert.assertNotNull(_hydra_applicatons);
		Assert.assertTrue(_hydra_applicatons instanceof WebApplications);		
	}	
	
}