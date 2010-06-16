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

public class TestFMDKspCf {
	/**
	 * FMD - (Find, Mutate(Insert/Update), Delete) 
	 */
	public static final int testUsersCount = 5;
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

	@AfterClass
	public static void clearTestUsers() {
		Utils4Tests.deleteAllTestUsers();
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
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, Utils4Tests.KSMAINTEST_Users);
		// 1.2.2 request data from db
		ResultAsListOfColumnOrSuperColumn result = accessor.get4KspCf(testPath);
		// 1.2.3 test result
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);
		// 2. !!!------------------ Mutate ------------------ !!!
		testUsersMap = Utils4Tests.initTestUsers(testUsersCount);
		// 2.1 test local test map size
		Assert.assertTrue(testUsersMap.size() == testUsersCount);
		// 2.2 !!!------------------ FIND ------------------!!!
		result = accessor.get4KspCf(testPath);
		// 2.3 test result
		Assert.assertTrue(result.isOk());
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == testUsersCount);
		// 2.4 compare local & cassandra'a data
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
			Assert.assertNotNull(mapStringByteA.get(Utils4Tests.PASSWORD));
			Assert.assertEquals(String.format(Utils4Tests.S_PASSWORD, ID1), 
					DBUtils.bytes2UTF8String(mapStringByteA.get(Utils4Tests.PASSWORD)));
		}
		// print result - debug
		// DBUtils.printResult(result);
		// 3. !!!------------------ Delete ------------------!!!
		clearTestUsers();
		// 3.1 get data from db
		result = accessor.get4KspCf(testPath);
		// 1.2.3 test result
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);		
	}

}