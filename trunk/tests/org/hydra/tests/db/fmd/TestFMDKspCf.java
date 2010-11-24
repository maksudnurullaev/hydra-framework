package org.hydra.tests.db.fmd;

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.tests.bean.Test0;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class TestFMDKspCf extends Test0 {
	/**
	 * FMD - (Find, Mutate(Insert/Update), Delete) 
	 */
	public static final int testUsersCount = 5;
	static Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
	
	@AfterClass
	public static void clearTestUsers() {
		Utils4Tests.deleteAllTestUsers();
	}
		
	@Test
	public void test_1_users(){
		// 1. FMD - Find (EMPTY RESULT)
		// 1.1 clear users data
		clearTestUsers();
		// 1.2 request users data
		// 1.2.1 create cassadnra's virtual path
		CassandraVirtualPath path = new CassandraVirtualPath(_cassandraDescriptor, Utils4Tests.KSMAINTEST_Users);
		// 1.2.2 request data from db
		ResultAsListOfColumnOrSuperColumn result = _cassandraAccessor.find(path);
		// 1.2.3 test result
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);
		// 2. !!!------------------ Mutate ------------------ !!!
		// ... init test users
		Map<String, Map<String, String>> users = Utils4Tests.initTestUsers(testUsersCount);
			
		// ... create access path for batch insert
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertNotNull(path._kspBean);
		Assert.assertNotNull(path._cfBean);
		Assert.assertNotNull(users);
		
		// ... send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = _cassandraAccessor.update(path, DBUtils.convert2Bytes(users));
		
		// Test result
		Assert.assertTrue(batchInsertResult.isOk());		
		// 2.1 test local test map size
		Assert.assertTrue(users.size() == testUsersCount);
		// 2.2 !!!------------------ FIND ------------------!!!
		result = _cassandraAccessor.find(path);
		// 2.3 test result
		Assert.assertTrue(result.isOk());
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == testUsersCount);
		// 2.4 compare local & cassandra'a data
		for(ColumnOrSuperColumn columnOrSuperColumn:result.getColumnOrSuperColumn()){
			String ID1 = DBUtils.bytes2UTF8String(columnOrSuperColumn.super_column.name);
			Assert.assertTrue(users.containsKey(ID1));
			Map<String, String> subMap = users.get(ID1);
			// test column and values
			Assert.assertNotNull(columnOrSuperColumn.super_column.getColumns());
			for(Column column: columnOrSuperColumn.super_column.getColumns()){
				String ID2 = DBUtils.bytes2UTF8String(column.name);
				Assert.assertTrue(subMap.containsKey(ID2));
				Assert.assertEquals(subMap.get(ID2), DBUtils.bytes2UTF8String(column.value));
			}
			Map<String, byte[]> mapStringByteA = DBUtils.converMapStringByteA(columnOrSuperColumn.super_column.getColumns());
			Assert.assertNotNull(mapStringByteA.get(Utils4Tests.USER_PASSWORD));
			Assert.assertEquals(String.format(Utils4Tests.USER_PASSWORD_S, ID1), 
					DBUtils.bytes2UTF8String(mapStringByteA.get(Utils4Tests.USER_PASSWORD)));
		}
		// print result - debug
		// DBUtils.printResult(result);
		// 3. !!!------------------ Delete ------------------!!!
		clearTestUsers();
		// 3.1 get data from db
		result = _cassandraAccessor.find(path);
		// 1.2.3 test result
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);		
	}

}