package org.hydra.tests.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraDescriptorBean;
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
		Assert.assertTrue(factory.containsBean(Constants._beans_cassandra_server_descriptor));
		Assert.assertTrue(factory.getBean(Constants._beans_cassandra_server_descriptor) instanceof CassandraDescriptorBean);
	}	
	
	@Test
	public void test_cassandraServer2(){
		CassandraDescriptorBean server = (CassandraDescriptorBean) factory.getBean(Constants._beans_cassandra_server_descriptor);
		Assert.assertNotNull(server.getHost());
		Assert.assertFalse(server.getPort() == -1);		
	}	
	
}
