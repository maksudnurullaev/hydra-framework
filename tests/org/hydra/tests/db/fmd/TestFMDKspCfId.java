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
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.hydra.utils.BeansUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFMDKspCfId {
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
		String format = "KSMainTEST.Users.%s";		
		// clean up users data
		clearAllTestUsers();
		//!!!------------------ FIND (nothing) ------------------!!!
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, 
				String.format(format, "ZZZ"));
		Assert.assertEquals(PATH_TYPE.KSP___CF___ID, path.getPathType());
		Assert.assertEquals(ERR_CODES.NO_ERROR, path.getErrorCode());
		ResultAsListOfColumnOrSuperColumn resultAsListOfUsers = accessor.find(path);
		Assert.assertTrue(resultAsListOfUsers.isOk());
		Assert.assertEquals(0, resultAsListOfUsers.getColumnOrSuperColumn().size());
		// create single user for test
		Map<String, Map<String, String>> user = Utils4Tests.initTestUsers(1);
		// .. get test user id		
		String userID = (String) user.keySet().toArray()[0];
		
			
		// create access path for batch insert
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) BeansUtils.getBean(Constants._beans_cassandra_descriptor);
		
		path = new CassandraVirtualPath(descriptor, Utils4Tests.KSMAINTEST_Users);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(user.containsKey(userID));
		Assert.assertTrue(user.size() == 1);
		Assert.assertTrue(DBUtils.validateFields(path._cfBean, user.get(userID)));
		
		// send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.update(path, DBUtils.convert2Bytes(user));
		
		// test result
		Assert.assertTrue(batchInsertResult.isOk());		
		
		Assert.assertTrue(user.size() == 1);
		//!!!------------------ FIND (single) ------------------!!!
		path = new CassandraVirtualPath(descriptor,	String.format(format, userID));
		resultAsListOfUsers = accessor.find(path);
		if(resultAsListOfUsers.getColumnOrSuperColumn().size() != 1){
			System.out.println("ERROR!!! Size: " + resultAsListOfUsers.getColumnOrSuperColumn().size());
			System.out.println("ERROR!!! Path: " + path.getPath());
		}
		Assert.assertEquals(1, resultAsListOfUsers.getColumnOrSuperColumn().size());
		SuperColumn superColumn = resultAsListOfUsers.getColumnOrSuperColumn().get(0).super_column;
		Assert.assertEquals(userID, DBUtils.bytes2UTF8String(superColumn.name));
		//!!!------------------ MUTATE (change) ------------------!!!
		Assert.assertTrue(user.get(userID).containsKey(Utils4Tests.USER_EMAIL));
		String testMail = "zzzz@zzz.zzz";
		user.get(userID).put(Utils4Tests.USER_EMAIL, testMail);
		batchInsertResult = accessor.update(path, DBUtils.convert2Bytes(user));
		Assert.assertTrue(batchInsertResult.isOk());
		resultAsListOfUsers = accessor.find(path);
		Map<String, byte[]> mapStringBytes = 
			DBUtils.converMapStringByteA(resultAsListOfUsers.getColumnOrSuperColumn().get(0).super_column.columns);
		Assert.assertTrue(mapStringBytes.containsKey(Utils4Tests.USER_EMAIL));
		Assert.assertEquals(testMail, DBUtils.bytes2UTF8String(mapStringBytes.get(Utils4Tests.USER_EMAIL)));
		//TODO !!!------------------ DELETE ------------------!!!
		Result delColResult = accessor.delete(path);
		Assert.assertTrue(delColResult.isOk());		
		resultAsListOfUsers = accessor.find(path);
		Assert.assertTrue(resultAsListOfUsers.getColumnOrSuperColumn().size() == 0);		
	}

}
