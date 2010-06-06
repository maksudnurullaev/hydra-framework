package org.hydra.tests.bean;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsMapOfStringAndColumnBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestDescriptor {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory beanFactory = Utils4Tests.getBeanFactory();
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
	
	@Test
	public void test_1_COLUMNS_bean_access_from_vpath(){
		CassandraVirtualPath testPath = new CassandraVirtualPath(cassandraDescriptor, "KSMainTEST.Users.COLUMNS");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		
		Assert.assertEquals("KSMainTEST", testPath.getPathPart(PARTS.P1_KSP));
		Assert.assertEquals("Users", testPath.getPathPart(PARTS.P2_CF));
		Assert.assertEquals(PATH_TYPE.KSP___CF___COLUMNS, testPath.getPathType());
		
		ResultAsMapOfStringAndColumnBean result = cassandraDescriptor.resultAsMapOfColumns(testPath);
		
		Map<String, ColumnBean>  resultMap =  result.getMapOfStringAndColumnBean();
		Assert.assertTrue(resultMap.size() == 2);
		Assert.assertTrue(resultMap.containsKey("Password"));
		Assert.assertTrue(resultMap.containsKey("Email"));
	}

	@Test
	public void test_1_LINKS_bean_access_from_vpath(){
		CassandraVirtualPath testPath = new CassandraVirtualPath(cassandraDescriptor, "KSMainTEST.Users.LINKS");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		
		Assert.assertEquals("KSMainTEST", testPath.getPathPart(PARTS.P1_KSP));
		Assert.assertEquals("Users", testPath.getPathPart(PARTS.P2_CF));
		Assert.assertEquals(PATH_TYPE.KSP___CF___LINKS, testPath.getPathType());
		
		ResultAsMapOfStringAndColumnBean result = cassandraDescriptor.resultAsMapOfLinks(testPath);
		
		Map<String,ColumnBean> resultMap = result.getMapOfStringAndColumnBean();
		Assert.assertTrue(resultMap.size() == 2);
		Assert.assertTrue(resultMap.containsKey("Articles"));
		Assert.assertTrue(resultMap.containsKey("Comments"));
	}
}
