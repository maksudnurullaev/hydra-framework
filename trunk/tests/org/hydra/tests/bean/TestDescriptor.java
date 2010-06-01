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

public class TestDescriptor {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory beanFactory = Utils4Tests.getBeanFactory();

	@Before
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(_log);
	}
	
	@Test
	public void test_beans(){
		Assert.assertTrue(beanFactory.containsBean(Constants._beans_cassandra_descriptor));
		Assert.assertTrue(beanFactory.getBean(Constants._beans_cassandra_descriptor) instanceof CassandraDescriptorBean);
				
		CassandraDescriptorBean cassandraDescriptorBean = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
		Assert.assertNotNull(cassandraDescriptorBean);
		
	}

}
