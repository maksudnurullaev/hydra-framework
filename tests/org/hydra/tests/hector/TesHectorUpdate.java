package org.hydra.tests.hector;

import me.prettyprint.hector.api.factory.HFactory;

import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Test;

public class TesHectorUpdate {	
	@Test
	public void test_cluster(){
		Object o = BeansUtils4Tests.getBean("kspHydraUz");
		if(o instanceof HFactory){	
			HFactory s = (HFactory) o;
			System.out.println(s.toString());
		}
	}
}
