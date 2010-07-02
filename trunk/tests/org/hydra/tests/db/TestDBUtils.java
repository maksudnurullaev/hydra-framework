package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.bean.TestVirtualPath;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.BeansUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestDBUtils {
	
	static Map<String, Map<String, String>> testUserMap = new HashMap<String, Map<String, String>>();
	static Map<String, Map<String, String>> testArticleMap = new HashMap<String, Map<String, String>>();
	
	Log _log = LogFactory.getLog(this.getClass());
	static BeanFactory beanFactory = BeansUtils.getBeanFactory();
	static CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
	
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(descriptor);		
	}
	@Test
	public void test_1_simple() {
		// 1. setup fields
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap.put("Title", "Title text");
		fieldValueMap.put("Text", "Article text");
		// 2. setup cf's
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		// test against users cf
		Assert.assertFalse(DBUtils.validateFields(path._cfBean, fieldValueMap));
		// test against articles cf
		Assert.assertTrue(DBUtils.validateFields(path._cfLinkBean, fieldValueMap));
	}
}
