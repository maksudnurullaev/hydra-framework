package org.hydra.tests.db.fmd;

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.hydra.utils.CryptoManager;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestFMDKspCfId {
	/**
	 * FMD - (Find, Mutate(Insert/Update), Delete) 
	 */
	private static final String TEST_S_MAIL_COM = "test%s@mail.com";
	public static final String KSTestUsersId = "KSMainTEST.Users.userID";
	public static final String USERID = "userID";
	private static final String PASSWORD = "Password";
	private static final String EMAIL = "Email";
	static Map<String, Map<String, String>> testUsersMap = new HashMap<String, Map<String, String>>();
	
	Log _log = LogFactory.getLog(this.getClass());
	static BeanFactory beanFactory = Utils4Tests.getBeanFactory();
	static CassandraAccessorBean accessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
	static CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
	
	@BeforeClass
	public static void initTestData(){
		if(!accessor.isValid())
			accessor.setup();		
	}
	
	private static void initTestUser() {
		// 1. Iterate over the user count and create Map<String, Map<String,String>> for batch insert
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put(PASSWORD, CryptoManager.encryptPassword(USERID));
		tempMap.put(EMAIL, TEST_S_MAIL_COM);		
		testUsersMap.put(USERID, tempMap);
		// 2. Create access path for batch insert
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUsersId);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(DBUtils.validateCfAndMap(path._cfBean, tempMap));
		// 3. Send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(testUsersMap));
		// 4. Test result
		Assert.assertTrue(batchInsertResult.isOk());
	}


	@AfterClass
	public static void clearTestUsers() {
//		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUsersId);
//		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
//		Assert.assertTrue(path._kspBean != null);
//		Assert.assertTrue(path._cfBean != null);
//		accessor.batchDelete4KspCf(path);
	}
	
	@Before
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(_log);
		Assert.assertNotNull(accessor);
		Assert.assertNotNull(descriptor);		
	}
	
	@Test
	public void test_1_users(){
		// 1. FMD - Find (EMPTY RESULT)
		// 1.1 clear users data
		clearTestUsers();
		// 1.2 request users data
		// 1.2.1 create cassadnra's virtual path
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, KSTestUsersId);
		// 1.2.2 request data from db
		ResultAsListOfColumnOrSuperColumn result = accessor.get4KspCfId(testPath);
		// 1.2.3 test result
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);
		// 2. FMD - Mutate
		initTestUser();
		// 2.1 test local test map size
		Assert.assertTrue(testUsersMap.size() == 1);
//		// 2.2 request data from db
//		result = accessor.resultAsListOfColumns4KspCf(testPath);
//		// 2.3 test result
//		Assert.assertTrue(result.isOk());
//		Assert.assertTrue(result.getColumnOrSuperColumn().size() == testUsersCount);
//		// 2.4 compare local & cassandra'a data
//		for(ColumnOrSuperColumn columnOrSuperColumn:result.getColumnOrSuperColumn()){
//			String ID1 = DBUtils.bytes2UTF8String(columnOrSuperColumn.super_column.name);
//			Assert.assertTrue(testUsersMap.containsKey(ID1));
//			Map<String, String> subMap = testUsersMap.get(ID1);
//			// test column and values
//			Assert.assertNotNull(columnOrSuperColumn.super_column.getColumns());
//			for(Column column: columnOrSuperColumn.super_column.getColumns()){
//				String ID2 = DBUtils.bytes2UTF8String(column.name);
//				Assert.assertTrue(subMap.containsKey(ID2));
//				Assert.assertEquals(subMap.get(ID2), DBUtils.bytes2UTF8String(column.value));
//			}
//			Map<String, byte[]> mapStringByteA = DBUtils.converMapStringByteA(columnOrSuperColumn.super_column.getColumns());
//			Assert.assertNotNull(mapStringByteA.get(PASSWORD));
//			Assert.assertTrue(CryptoManager.checkPassword(ID1, DBUtils.bytes2UTF8String(mapStringByteA.get(PASSWORD))));
//		}
//		// print result - debug
//		// DBUtils.printResult(result);
//		// 3. FMD - Delete
//		clearTestUsers();
//		// 3.1 get data from db
//		result = accessor.resultAsListOfColumns4KspCf(testPath);
//		// 1.2.3 test result
//		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);		
	}

}
