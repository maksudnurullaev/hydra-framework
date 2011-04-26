package org.hydra.tests.hector;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.Test;

public class TesHectorUpdate {	
	@Test
	public void test_cluster(){
		Object o = BeansUtils4Tests.getBean("cfHydraUzText");
		Assert.assertTrue(o instanceof SimpleCassandraDao);	
		SimpleCassandraDao s = (SimpleCassandraDao) o;
		//s.insert("key", "eng", "eng_value");
		//s.insert("key", "rus", "rus_value");
		//System.out.println(s.get("key", "rus"));
		System.out.println(s.get("testID", "eng"));
		
	}
}
