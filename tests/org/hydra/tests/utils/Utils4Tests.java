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
	public static final String USER_S = "user%s";
	public static final String S_PASSWORD = "%sPassword";
	public static final String PASSWORD = "Password";
	public static final String EMAIL = "Email";
	public static final String TEST_S_MAIL_COM = "test%s@mail.com";
	public static final String KSMAINTEST_Users = "KSMainTEST.Users";
	public static final String KSMAINTEST_Articles = "KSMainTEST.Articles";
	
	public final static Resource res = new FileSystemResource(Constants._path2ApplicationContext_xml);
	public static XmlBeanFactory factory = new XmlBeanFactory(res);
	
	
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
			userID = String.format(USER_S, i); //Constants.GetDateUUIDTEST();

			tempMap = new HashMap<String, String>();
			
			tempMap.put(PASSWORD, String.format(S_PASSWORD, userID)); // CryptoManager.encryptPassword(userID));
			tempMap.put(EMAIL, String.format(TEST_S_MAIL_COM, i));
			
			result.put(userID, tempMap);
		}
		
		// 2. Create access path for batch insert
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSMAINTEST_Users);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(DBUtils.validateCfAndMap(path._cfBean, tempMap));
		// 3. Send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.update(path, DBUtils.convertMapKBytesVMapKBytesVBytes(result));
		
		// 4. Test result
		Assert.assertTrue(batchInsertResult.isOk());
		
		return result;
	}

	public static Result deleteAllTestUsers() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) getBean(Constants._beans_cassandra_accessor);
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) getBean(Constants._beans_cassandra_descriptor);
		
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSMAINTEST_Users);
		
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		return accessor.delete(path);		
	}
	
	public static CassandraAccessorBean getAccessor() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) Utils4Tests.getBean(Constants._beans_cassandra_accessor);
		if(!accessor.isValid()) accessor.setup();
		return accessor;
	}

	public static CassandraDescriptorBean getDescriptor() {
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) Utils4Tests.getBean(Constants._beans_cassandra_descriptor);
		return descriptor;
	}

	public static Result deleteAllTestArticles() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) getBean(Constants._beans_cassandra_accessor);
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) getBean(Constants._beans_cassandra_descriptor);
		
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSMAINTEST_Articles);
		
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		return accessor.delete(path);		
	}	
}
