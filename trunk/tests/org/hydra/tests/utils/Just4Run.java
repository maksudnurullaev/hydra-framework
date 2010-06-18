package org.hydra.tests.utils;

import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;


public class Just4Run {

	public static void main(String[] args) {
		Map<String, Map<String, String>> result = Utils4Tests.initTestUsers(10);
		printUsers();
		// get accessor
		CassandraAccessorBean accessorBean = Utils4Tests.getAccessor();
		CassandraDescriptorBean descriptor = Utils4Tests.getDescriptor();
		
		// access path formater
		String format = "KSMainTEST.Users.%s";
		// find all records one by one & delete them with checking deletion
		for(Map.Entry<String, Map<String, String>> mapKeyMapNameValue:result.entrySet()){
			CassandraVirtualPath tempPath = new CassandraVirtualPath(descriptor, 
					String.format(format, mapKeyMapNameValue.getKey()));
			
			Assert.assertTrue(tempPath.getErrorCode() == ERR_CODES.NO_ERROR);
			
			ResultAsListOfColumnOrSuperColumn findColResult = accessorBean.get4Path(tempPath);
			Assert.assertTrue(findColResult.isOk());
			Assert.assertTrue(findColResult.getColumnOrSuperColumn().size() == 1);
			
			// test column values
			ColumnOrSuperColumn columnOrSuperColumn = findColResult.getColumnOrSuperColumn().get(0);
			for(Column column: columnOrSuperColumn.getSuper_column().columns){
				String name = DBUtils.bytes2UTF8String(column.name);
				String value = DBUtils.bytes2UTF8String(column.value);
				Assert.assertTrue(mapKeyMapNameValue.getValue().containsKey(name));
				Assert.assertEquals(mapKeyMapNameValue.getValue().get(name), value);
			}
			
			// try delete column
			Result delColResult = accessorBean.delete4KspCfId(tempPath);
			Assert.assertTrue(delColResult.isOk());
			
			// test column
			findColResult = accessorBean.get4Path(tempPath);
			Assert.assertTrue(findColResult.isOk());
			Assert.assertTrue(findColResult.getColumnOrSuperColumn().size() == 0);
		}
		//Utils4Tests.deleteAllTestUsers();
		printUsers();
			
	}

	private static void printUsers() {
		CassandraDescriptorBean descriptor = Utils4Tests.getDescriptor();
		CassandraAccessorBean accessor = Utils4Tests.getAccessor();
		CassandraVirtualPath testUsersPath = new CassandraVirtualPath(descriptor, "KSMainTEST.Users");
		
		ResultAsListOfColumnOrSuperColumn resultUsers = accessor.get4Path(testUsersPath);
	
		System.out.println("TEST COLUMN COUNT: " + resultUsers.getColumnOrSuperColumn().size());
	}
}