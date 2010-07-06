package org.hydra.tests.db.fmd;

import java.util.Map;

import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFMDKspCfId {
	private static final String KS_MAIN_TEST_USERS_S = "KSMainTEST--->Users--->%s";

	/**
	 * FMD - (Find, Mutate(Insert/Update), Delete) 
	 */
	static Map<String, Map<String, String>> testUsersMap = null;
	
	Log _log = LogFactory.getLog(this.getClass());
	static CassandraAccessorBean accessor = DBUtils.getAccessor();
	static CassandraDescriptorBean descriptor = DBUtils.getDescriptor();
	
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
		// clean up users data
		clearAllTestUsers();
		
		// ***FIND*** - nothing
		CassandraVirtualPath path2UnkownUser = new CassandraVirtualPath(descriptor, 
				String.format(KS_MAIN_TEST_USERS_S, "ZZZ"));
		ResultAsListOfColumnOrSuperColumn result = accessor.find(path2UnkownUser);
		Assert.assertTrue(result.isOk());
		Assert.assertEquals(0, result.getColumnOrSuperColumn().size());
		
		// ***MUTATE*** - add new user
		Map<String, Map<String, String>> userMap = Utils4Tests.initTestUsers(1);
		// ... get test user id		
		String userID = (String) userMap.keySet().toArray()[0];
		// ... setup path
		CassandraVirtualPath path2Users = new CassandraVirtualPath(descriptor, Utils4Tests.KSMAINTEST_Users);
		// ... mutate
		Result batchInsertResult = accessor.update(path2Users, DBUtils.convert2Bytes(userMap));
		// ... test result
		Assert.assertTrue(batchInsertResult.isOk());		
		Assert.assertTrue(userMap.size() == 1);
		
		// ***FIND*** - user
		CassandraVirtualPath path2User = new CassandraVirtualPath(descriptor,	String.format(KS_MAIN_TEST_USERS_S, userID));
		result = accessor.find(path2User);
		// ... test result
		Assert.assertEquals(1, result.getColumnOrSuperColumn().size());
		
		SuperColumn superColumn = result.getColumnOrSuperColumn().get(0).super_column;
		Assert.assertEquals(userID, DBUtils.bytes2UTF8String(superColumn.name));
		
		// ***MUTATE*** - change user's email address
		String newMail = "zzzz@zzz.zzz";
		userMap.get(userID).put(Utils4Tests.USER_EMAIL, newMail);
		batchInsertResult = accessor.update(path2User, DBUtils.convert2Bytes(userMap));
		Assert.assertTrue(batchInsertResult.isOk());
		result = accessor.find(path2User);
		Map<String, byte[]> mapNameValue = 
			DBUtils.converMapStringByteA(result.getColumnOrSuperColumn().get(0).super_column.columns);
		Assert.assertTrue(mapNameValue.containsKey(Utils4Tests.USER_EMAIL));
		Assert.assertEquals(newMail, DBUtils.bytes2UTF8String(mapNameValue.get(Utils4Tests.USER_EMAIL)));
		
		// ***FIND*** - nothing
		Result delColResult = accessor.delete(path2User);
		Assert.assertTrue(delColResult.isOk());		
		result = accessor.find(path2User);
		Assert.assertTrue(result.getColumnOrSuperColumn().size() == 0);		
	}

}
