package org.hydra.tests.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestSpringCassandraServerBean {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = Utils4Tests.getBeanFactory();

	@Before
	public void test_main() {
		Assert.assertNotNull(factory);
	}
	
	@Test
	public void test_cassandraServer(){
		Assert.assertTrue(factory.containsBean(Constants._beans_cassandra_descriptor));
		Assert.assertTrue(factory.getBean(Constants._beans_cassandra_descriptor) instanceof ACassandraDescriptorBean);
	}	
	
	@Test
	public void test_cassandraServer2(){
		CassandraAccessorBean accessor = (CassandraAccessorBean) factory.getBean(Constants._beans_cassandra_accessor);
		Assert.assertNotNull(accessor.getDescriptor());
	}	
	
}
