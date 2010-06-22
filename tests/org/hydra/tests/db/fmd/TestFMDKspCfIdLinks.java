package org.hydra.tests.db.fmd;

import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFMDKspCfIdLinks {
	/**
	 * FUD - (Find, Update, Delete) 
	 */
	static Map<String, Map<String, String>> testUsersMap = null;
	
	Log _log = LogFactory.getLog(this.getClass());
	static CassandraAccessorBean accessor = Utils4Tests.getAccessor();
	static CassandraDescriptorBean descriptor = Utils4Tests.getDescriptor();
		
	@Before
	public void before(){
		Assert.assertNotNull(_log);
		Assert.assertNotNull(accessor);
		Assert.assertNotNull(descriptor);		
	}
	
	@Test
	public void test_1_users(){
		String format = "KSMainTEST.Users.%s.Articles";		
		// clean up data
		// ... users
		Result resultOfDeletionAll = Utils4Tests.deleteAllTestUsers();
		Assert.assertTrue(resultOfDeletionAll.isOk());
		// ... articles
		resultOfDeletionAll = Utils4Tests.deleteAllTestArticles();
		Assert.assertTrue(resultOfDeletionAll.isOk());
		// create single user for test
		Map<String, Map<String, String>> resultMapStringMapStringString = Utils4Tests.initTestUsers(1);
		Assert.assertTrue(resultMapStringMapStringString.size() == 1);
		// .. get test user id
		String userID = (String) resultMapStringMapStringString.keySet().toArray()[0];
		//TODO !!!------------------ FIND links (nothing) ------------------!!!
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor,	String.format(format, userID));
		ResultAsListOfColumnOrSuperColumn resultAsListOfUsers = accessor.find(path);
		Assert.assertTrue(resultAsListOfUsers.isOk());
		
//		Assert.assertEquals(1, resultAsListOfUsers.getColumnOrSuperColumn().size());
//		SuperColumn superColumn = resultAsListOfUsers.getColumnOrSuperColumn().get(0).super_column;
//		Assert.assertEquals(userID, DBUtils.bytes2UTF8String(superColumn.name));
//		//TODO !!!------------------ FIND links(all) ------------------!!!
//		//TODO !!!------------------ MUTATE (change articles) ------------------!!!
//		Assert.assertTrue(resultMapStringMapStringString.get(userID).containsKey(Utils4Tests.EMAIL));
//		String testMail = "zzzz@zzz.zzz";
//		resultMapStringMapStringString.get(userID).put(Utils4Tests.EMAIL, testMail);
//		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapKBytesVMapKBytesVBytes(resultMapStringMapStringString));
//		Assert.assertTrue(batchInsertResult.isOk());
//		resultAsListOfUsers = accessor.get4KspCfId(path);
//		Map<String, byte[]> mapStringBytes = 
//			DBUtils.converMapStringByteA(resultAsListOfUsers.getColumnOrSuperColumn().get(0).super_column.columns);
//		Assert.assertTrue(mapStringBytes.containsKey(Utils4Tests.EMAIL));
//		Assert.assertEquals(testMail, DBUtils.bytes2UTF8String(mapStringBytes.get(Utils4Tests.EMAIL)));
//		//TODO !!!------------------ DELETE(delete links) ------------------!!!
//		Result delColResult = accessor.delete4KspCfId(path);
//		Assert.assertTrue(delColResult.isOk());		
//		resultAsListOfUsers = accessor.get4KspCfId(path);
//		Assert.assertTrue(resultAsListOfUsers.getColumnOrSuperColumn().size() == 0);		
	}

}
