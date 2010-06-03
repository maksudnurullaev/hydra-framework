package org.hydra.tests.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestAccessor {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory beanFactory = Utils4Tests.getBeanFactory();
	CassandraAccessorBean testAccessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
	CassandraDescriptorBean testDescriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);

	@Before
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(_log);
		Assert.assertNotNull(testAccessor);
		Assert.assertNotNull(testDescriptor);
		
		if(!testAccessor.isValid())
			testAccessor.setup();
	}
	
	@Test
	public void test_beans(){
		Assert.assertTrue(testAccessor.isValid());
		
		/* Test accessor*/
		CassandraVirtualPath testPath = new CassandraVirtualPath(testDescriptor, "KSMainTEST.Users");
		Result result = testAccessor.getDBColumns(testPath);
	}

}
