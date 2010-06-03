package org.hydra.tests.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.hydra.utils.CryptoManager;
import org.hydra.utils.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
//import org.apache.cassandra.thrift.ColumnOrSuperColumn;

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
	public void test_getIDs(){
		Assert.assertTrue(testAccessor.isValid());
		
		initTestUsers(5);
		
//		/* Test accessor */
//		CassandraVirtualPath testPath = new CassandraVirtualPath(testDescriptor, "KSMainTEST.Users");
//		Result result = testAccessor.getDBColumns(testPath);
//		
//		if(result.isOk()){
//			if(result.getObject() instanceof List){
//				List<?> resultList = (List<?>) result.getObject();
//				if(resultList.size() > 0){
//					System.out.println("Result list of ColumnOrSuperColumn is empty!");					
//				}else{
//					System.out.println("Result list of ColumnOrSuperColumn is empty!");
//				}
//			}else{
//				System.out.println("Result UNKNOWN instead of List<ColumnOrSuperColumn>!");				
//			}
//		}
//		System.out.println(result.isOk());
	}

	private Map<String, Map<String,String>> initTestUsers(int count) {		
		Map<String, Map<String,String>> result = new HashMap<String, Map<String,String>>();

		CassandraVirtualPath testPath = new CassandraVirtualPath(testDescriptor, "KSMainTEST.Users.COLUMNS");
		Assert.assertTrue(testPath.cfBean != null);
		
		// 1. Iterate over count of users and create Map<String, Map<String,String>> to bach insert
		String userID = null;
		Map<String, String> tempMap;
		for (int i = 0; i < count; i++) {
			userID = Constants.GetDateUUIDTEST();
			System.out.println(userID);

			tempMap = new HashMap<String, String>();
			tempMap.put("Password", CryptoManager.encryptPassword(userID));
			tempMap.put("Email", String.format("test%s@mail.com", i));
			
			result.put(userID, tempMap);
		}
		// 2. Get fields descriptions for user descriptions
		// 3. Insert list of user [-- Map<IDs, Map<fields, values> --] into Cassandra DB
		// 4. return result
		// Need te get field description from descriptions bean
		
		return result;		
	}

}
