package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Iterator;
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

public class TestAccessor {
	private static final String PASSWORD = "Password";
	private static final String EMAIL = "Email";
	private static final String TEST_S_MAIL_COM = "test%s@mail.com";
	public static final String KSTestUserPath = "KSMainTEST.Users";
	public static final int testUsersCount = 5;
	static Map<String, Map<String, String>> testUsersMap = new HashMap<String, Map<String, String>>();
	
	Log _log = LogFactory.getLog(this.getClass());
	static BeanFactory beanFactory = Utils4Tests.getBeanFactory();
	static CassandraAccessorBean accessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
	static CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
	
	@BeforeClass
	public static void initTestData(){
		initTestUsers();
	}

	private static void initTestUsers() {
		// 0. setup accessor
		if(!accessor.isValid())
			accessor.setup();		
		// 1. Iterate over the user count and create Map<String, Map<String,String>> for batch insert
		String userID = null;
		Map<String, String> tempMap;
		for (int i = 0; i < testUsersCount; i++) {
			userID = Constants.GetDateUUIDTEST();

			tempMap = new HashMap<String, String>();
			
			tempMap.put(PASSWORD, CryptoManager.encryptPassword(userID));
			tempMap.put(EMAIL, String.format(TEST_S_MAIL_COM, i));
			
			testUsersMap.put(userID, tempMap);
		}
		
		// 2. Create access path for batch insert
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUserPath);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		
		// 3. Send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(testUsersMap));
		
		// 4. Test result
		Assert.assertTrue(batchInsertResult.isOk());
	}

	@AfterClass
	public static void clearTestData(){
		clearTestUsers();		
	}

	private static void clearTestUsers() {
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUserPath);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		
		accessor.batchDelete4KspCf(path);
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
		Assert.assertTrue(testUsersMap.size() == testUsersCount);
		// get users from database
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, KSTestUserPath);
		ResultAsListOfColumnOrSuperColumn result = accessor.resultAsListOfColumns4KspCf(testPath);
		// test result
		Assert.assertTrue(result.isOk());
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == testUsersCount);
		// test compare resultMap & result
		
		for(ColumnOrSuperColumn columnOrSuperColumn:result.getColumnOrSuperColumn()){
			String ID1 = DBUtils.bytes2UTF8String(columnOrSuperColumn.super_column.name);
			Assert.assertTrue(testUsersMap.containsKey(ID1));
			Map<String, String> subMap = testUsersMap.get(ID1);
			// test column and values
			Assert.assertNotNull(columnOrSuperColumn.super_column.getColumns());
			for(Column column: columnOrSuperColumn.super_column.getColumns()){
				String ID2 = DBUtils.bytes2UTF8String(column.name);
				Assert.assertTrue(subMap.containsKey(ID2));
				Assert.assertEquals(subMap.get(ID2), DBUtils.bytes2UTF8String(column.value));
			}
			Map<String, byte[]> mapStringByteA = DBUtils.converMapStringByteA(columnOrSuperColumn.super_column.getColumns());
			Assert.assertNotNull(mapStringByteA.get(PASSWORD));
			Assert.assertTrue(CryptoManager.checkPassword(ID1, DBUtils.bytes2UTF8String(mapStringByteA.get(PASSWORD))));
		}
		// print result
		// printResult(result);
	}

	public void printResult(ResultAsListOfColumnOrSuperColumn result) {
		if(result.getColumnOrSuperColumn() != null ||
				result.getColumnOrSuperColumn().size() != 0){
			Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
			while(listIterator.hasNext()){
				ColumnOrSuperColumn superColumn = listIterator.next();
				Assert.assertTrue(superColumn.isSetSuper_column());
				System.out.println(String.format("SuperCol.Name = %s\n",
						DBUtils.bytes2UTF8String(superColumn.super_column.name, 32)));											
				for(Column column:superColumn.getSuper_column().columns){
					System.out.println(String.format("--> Col.Name = %s\n----> Value = %s\n----> Timestamp = %s\n",
							DBUtils.bytes2UTF8String(column.name, 32), 
							DBUtils.bytes2UTF8String(column.value, 32),
							column.timestamp));							
				}
			}
			System.out.println("TEST COLUMN COUNT: " + result.getColumnOrSuperColumn().size()); 
		}else{
			System.out.println("Result is NULL or EMPTY!");				
		}
	}


}
