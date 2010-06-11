package org.hydra.tests.db.fmd;

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
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFMDKspCfId {
	/**
	 * FMD - (Find, Mutate(Insert/Update), Delete) 
	 */
	static Map<String, Map<String, String>> testUsersMap = null;
	
	Log _log = LogFactory.getLog(this.getClass());
	static CassandraAccessorBean accessor = Utils4Tests.getAccessor();
	static CassandraDescriptorBean descriptor = Utils4Tests.getDescriptor();
	
	public static void clearAllTestUsers() {
		// clean up users data
		Result resultOfDeletionAll = Utils4Tests.deleteAllTestUsers();
		Assert.assertTrue(resultOfDeletionAll.isOk());
	}
	
	@Before
	public void before(){
		Assert.assertNotNull(_log);
		Assert.assertNotNull(accessor);
		Assert.assertNotNull(descriptor);		
	}
	
	@Test
	public void test_1_users(){
		int countOfTestUsers = 10;
		// clean up users data
		clearAllTestUsers();
		//!!!------------------ MUTATE ------------------!!!
		Map<String, Map<String, String>> resultInsertionOfUsers = Utils4Tests.initTestUsers(countOfTestUsers);
		Assert.assertTrue(resultInsertionOfUsers.size() == countOfTestUsers);
		
		// access path formater
		String format = "KSMainTEST.Users.%s";
		// one by one - find/check/delete column
		for(Map.Entry<String, Map<String, String>> mapKeyMapNameValue:resultInsertionOfUsers.entrySet()){
			CassandraVirtualPath tempPath = new CassandraVirtualPath(descriptor, 
					String.format(format, mapKeyMapNameValue.getKey()));
			
			Assert.assertTrue(tempPath.getErrorCode() == ERR_CODES.NO_ERROR);
			// !!!------------------ FIND ------------------!!!
			ResultAsListOfColumnOrSuperColumn findColResult = accessor.get4KspCfId(tempPath);
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
			
			// !!!------------------ DELETE ------------------!!!
			Result delColResult = accessor.delete4KspCfId(tempPath);
			Assert.assertTrue(delColResult.isOk());
			
			// test column
			findColResult = accessor.get4KspCfId(tempPath);
			Assert.assertTrue(findColResult.isOk());
			Assert.assertTrue(findColResult.getColumnOrSuperColumn().size() == 0);
		}
		// Check that cf(Users) is know empty
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor,Utils4Tests.KSMAINTEST_Users);
		Assert.assertTrue(testPath.isValid());
		
		ResultAsListOfColumnOrSuperColumn resultAsListOfColumnOrSuperColumn = accessor.get4KspCf(testPath);
		Assert.assertTrue(resultAsListOfColumnOrSuperColumn.getColumnOrSuperColumn().size() == 0);
		
	}

}
