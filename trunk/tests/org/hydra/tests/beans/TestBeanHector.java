package org.hydra.tests.beans;


import me.prettyprint.hector.api.Cluster;

import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.Test;

public class TestBeanHector {	
	@Test
	public void test_mainPipe(){
		Object o = BeansUtils4Tests.getBean("_cassnadra_cluster");
		Assert.assertTrue(o instanceof Cluster);		
	}	
}
