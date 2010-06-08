package org.hydra.tests.utils;

import java.util.HashMap;
import java.util.Map;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.utils.Constants;
import org.hydra.utils.CryptoManager;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.junit.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * To use this test we should create 1000 test users
 * 
 * @see Create100TestUsers
 * @author M.Nurullayev
 */
public final class Utils4Tests {
	public static final String PASSWORD = "Password";
	public static final String EMAIL = "Email";
	public static final String TEST_S_MAIL_COM = "test%s@mail.com";
	public static final String KSTestUsers = "KSMainTEST.Users";
	
	final static Resource res = new FileSystemResource(Constants._path2ApplicationContext_xml);
	final static XmlBeanFactory factory = new XmlBeanFactory(res);
	
	public static BeanFactory getBeanFactory(){
		return factory;
	}
	
	public static Object getBean(String inName){
		return factory.getBean(inName);
	}
	
	public static Map<String, Map<String, String>> initTestUsers(int count) {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		
		CassandraAccessorBean accessor = (CassandraAccessorBean) getBean(Constants._beans_cassandra_accessor);
		if(!accessor.isValid())
			accessor.setup();
		
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) getBean(Constants._beans_cassandra_descriptor);
		
		// 1. Iterate over the user count and create Map<String, Map<String,String>> for batch insert
		String userID = null;
		Map<String, String> tempMap = null;
		for (int i = 0; i < count; i++) {
			userID = Constants.GetDateUUIDTEST();

			tempMap = new HashMap<String, String>();
			
			tempMap.put(PASSWORD, CryptoManager.encryptPassword(userID));
			tempMap.put(EMAIL, String.format(TEST_S_MAIL_COM, i));
			
			result.put(userID, tempMap);
		}
		
		// 2. Create access path for batch insert
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUsers);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(DBUtils.validateCfAndMap(path._cfBean, tempMap));
		// 3. Send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(result));
		
		// 4. Test result
		Assert.assertTrue(batchInsertResult.isOk());
		
		return result;
	}

	public static void deleteAllTestUsers() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) getBean(Constants._beans_cassandra_accessor);
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) getBean(Constants._beans_cassandra_descriptor);
		
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, Utils4Tests.KSTestUsers);
		
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		accessor.deleteAllKspCf(path);		
	}

	public static Map<String, Map<String, String>> initTestUser(String inUserId) {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		
		CassandraAccessorBean accessor = (CassandraAccessorBean) getBean(Constants._beans_cassandra_accessor);
		if(!accessor.isValid())
			accessor.setup();
		
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) getBean(Constants._beans_cassandra_descriptor);
		
		// 1. Iterate over the user count and create Map<String, Map<String,String>> for batch insert
		Map<String, String> tempMap = null;

		tempMap = new HashMap<String, String>();
		
		tempMap.put(PASSWORD, CryptoManager.encryptPassword(inUserId));
		tempMap.put(EMAIL, String.format(TEST_S_MAIL_COM, inUserId));
		
		result.put(inUserId, tempMap);
		
		// 2. Create access path for batch insert
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUsers);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(DBUtils.validateCfAndMap(path._cfBean, tempMap));
		// 3. Send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(result));
		
		// 4. Test result
		Assert.assertTrue(batchInsertResult.isOk());
		
		return result;
	}	
}
