package org.hydra.tests.db;

import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAccessor {
	private static final int testUsersCount = 5;
	static Map<String, Map<String, String>> users = null;

	static CassandraAccessorBean accessor = (CassandraAccessorBean) BeansUtils.getBean(Constants._beans_cassandra_accessor);
	static CassandraDescriptorBean descriptor = (CassandraDescriptorBean) BeansUtils.getBean(Constants._beans_cassandra_descriptor);
	static CassandraVirtualPath path = new CassandraVirtualPath(descriptor, Utils4Tests.KSMAINTEST_Users);	
	

	@Before
	public void initTest(){
		Utils4Tests.deleteAllTestUsers();
		initTestUsers();
	}

	@Test
	public void test_1_users(){
		
		Assert.assertTrue(users.size() == testUsersCount);
		// get users from database
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, Utils4Tests.KSMAINTEST_Users);
		ResultAsListOfColumnOrSuperColumn result = accessor.find(testPath);
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
				descriptor,
				Utils4Tests.KSMAINTEST_Users);
		Result result = accessor.update(path2Users, DBUtils.convert2Bytes(users));
		
		if(result.isOk())
			System.out.println("Initial user data merged!");
		else{
			System.out.println(result.getResult());
			return false;
		}
		
		return true;
				
	}		
}
