package org.hydra.tests.db.fmd;

import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.bean.Test0;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAccessor extends Test0{
	private static final int testUsersCount = 5;
	static Map<String, Map<String, String>> users = null;
	static CassandraVirtualPath path = new CassandraVirtualPath(_cassandraDescriptor, Utils4Tests.KSMAINTEST_Users);	
	

	@Before
	public void initTest(){
		Utils4Tests.deleteAllTestUsers();
		initTestUsers();
	}

	@Test
	public void test_1_users(){
		
		Assert.assertTrue(users.size() == testUsersCount);
		// get users from database
		CassandraVirtualPath testPath = new CassandraVirtualPath(_cassandraDescriptor, Utils4Tests.KSMAINTEST_Users);
		ResultAsListOfColumnOrSuperColumn result = _cassandraAccessor.find(testPath);
		// test result
		Assert.assertTrue(result.isOk());
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == testUsersCount);
		// test compare resultMap & result
		
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

		}
	}

	private static boolean initTestUsers() {
		users = Utils4Tests.initTestUsers(testUsersCount);
		CassandraVirtualPath path2Users = new CassandraVirtualPath(
				_cassandraDescriptor,
				Utils4Tests.KSMAINTEST_Users);
		Result result = _cassandraAccessor.update(path2Users, DBUtils.convert2Bytes(users));
		
		if(result.isOk())
			System.out.println("Initial user data merged!");
		else{
			System.out.println(result.getResult());
			return false;
		}
		
		return true;
				
	}		
}
