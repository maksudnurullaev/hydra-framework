package org.hydra.tests.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.utils.Constants;
import org.hydra.utils.BeansUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestDescriptor {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory beanFactory = BeansUtils.getBeanFactory();
	CassandraDescriptorBean cassandraDescriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
	CassandraAccessorBean cassandraAccessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
	
	@Before
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(_log);
		Assert.assertNotNull(cassandraDescriptor);
		Assert.assertNotNull(cassandraAccessor);
	}
	
	@Test
	public void test_0_beans(){
		Assert.assertTrue(beanFactory.containsBean(Constants._beans_cassandra_descriptor));
		Assert.assertTrue(beanFactory.getBean(Constants._beans_cassandra_descriptor) instanceof CassandraDescriptorBean);
				
		CassandraDescriptorBean cassandraDescriptorBean = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
		Assert.assertNotNull(cassandraDescriptorBean);
		
	}
	
}
