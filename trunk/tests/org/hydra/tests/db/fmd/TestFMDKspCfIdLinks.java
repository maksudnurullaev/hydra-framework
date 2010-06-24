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
		int articleCount = 2;
		// clean up data
		// ... users
		Result resultOfDeletionAll = Utils4Tests.deleteAllTestUsers();
		Assert.assertTrue(resultOfDeletionAll.isOk());
		// ... articles
		resultOfDeletionAll = Utils4Tests.deleteAllTestArticles();
		Assert.assertTrue(resultOfDeletionAll.isOk());
		// TODO ... links
		
		// create single user for test
		Map<String, Map<String, String>> user = Utils4Tests.initTestUsers(1);
		String userID = (String) user.keySet().toArray()[0];
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor,
				Utils4Tests.KSMAINTEST_Users);		
		Result result = accessor.update(path, DBUtils.convert2Bytes(user));		
		// ... test result
		Assert.assertTrue(result.isOk());
		
		// FIND links (nothing found)
		path = new CassandraVirtualPath(descriptor,
				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, userID));
		ResultAsListOfColumnOrSuperColumn dbResult = accessor.find(path);
		Assert.assertTrue(dbResult.isOk());
		//TODO [remove later] Assert.assertEquals(0, dbUser.getColumnOrSuperColumn().size());
		
		// UPDATE/INSERT (insert 2 articles for users)
		Map<String, Map<String, String>> articles = Utils4Tests.initTestArticles(articleCount);
		result = accessor.update(path, DBUtils.convert2Bytes(articles));
		Assert.assertTrue(result.isOk());
		
		// TODO UPDATE/CHANGE user's articles
		
		// FIND links(all)
		dbResult = accessor.find(path);
		Assert.assertTrue(dbResult.isOk());
		Assert.assertEquals(1, dbResult.getColumnOrSuperColumn().size());
		Assert.assertEquals(articleCount, dbResult.getColumnOrSuperColumn().get(0).super_column.columns.size());
		
		//TODO [debug only] DBUtils.printResult(dbResult);
		
		//TODO ... DELETE(delete links)
		Assert.assertEquals(String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, userID), path.getPath());
		Assert.assertEquals(PATH_TYPE.KSP___CF___ID___LINKNAME, path.getPathType());
		result = accessor.delete(path);
		
//		Result delColResult = accessor.delete4KspCfId(path);
//		Assert.assertTrue(delColResult.isOk());		
//		resultAsListOfUsers = accessor.get4KspCfId(path);
//		Assert.assertTrue(resultAsListOfUsers.getColumnOrSuperColumn().size() == 0);		
	}

}
