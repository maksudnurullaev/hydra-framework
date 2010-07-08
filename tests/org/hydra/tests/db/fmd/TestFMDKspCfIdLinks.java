package org.hydra.tests.db.fmd;

import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFMDKspCfIdLinks {
	/**
	 * FMD - (Find, Mutate/Delete)
	 */
	static Map<String, Map<String, String>> testUsersMap = null;

	Log _log = LogFactory.getLog(this.getClass());
	static CassandraAccessorBean accessor = DBUtils.getAccessor();
	static CassandraDescriptorBean descriptor = DBUtils.getDescriptor();

	private static String userID = null;

	@Before
	public void before() {
		shouldNotBeNullObjects();
		clearTestData();
	}

	@After
	public void after_test(){
		shouldNotBeNullObjects();
		clearTestData();		
	}

	@Test
	public void test_fmd4user_with_articles() {
		Result result = null;
		
		// ***MUTATE*** - mutate certain article				
		// ... init single user with articles
		Assert.assertTrue(initTestDataStage1User());
		Assert.assertTrue(initTestDataStage2Articles());

		// ... find  initial
		ResultAsListOfColumnOrSuperColumn dbResult = accessor
				.find(path2UsersIDArticles);
		Assert.assertTrue(dbResult.isOk());
		
		// ***FIND*** - all articles
		dbResult = accessor.find(path2UsersIDArticles);
		Assert.assertTrue(dbResult.isOk());
		Assert.assertEquals(1, dbResult.getColumnOrSuperColumn().size());
		
		List<ColumnOrSuperColumn> listOfCOSArticles = dbResult.getColumnOrSuperColumn();
		Assert.assertEquals(1, listOfCOSArticles.size());
		
		ColumnOrSuperColumn cOs = listOfCOSArticles.get(0);
		Assert.assertTrue(cOs.isSetSuper_column());
		
		SuperColumn superColumn = cOs.getSuper_column();
		Assert.assertEquals(articleCount, superColumn.columns.size());
		
		// ***MUTATE(DELETE) - delete user & his articles
//		Assert.assertEquals(String.format(
//				Utils4Tests.KSMAINTEST_Users_S_Articles, userID),
//				path2UsersIDArticles.getPath());
//		Assert.assertEquals(PATH_TYPE.KSP___CF___ID___LINKNAME,
//				path2UsersIDArticles.getPathType());
//
//		result = accessor.delete(path2User);
//		Assert.assertTrue(result.isOk());
//
//		// ... test deletes
//		dbResult = accessor.find(path2UsersIDArticles);
//		Assert.assertTrue(dbResult.isOk());
//		Assert.assertEquals(0, dbResult.getColumnOrSuperColumn().size());
	}

	// ### UTILS ### 
	private void clearTestData() {
		// clean up data
		// ... users
		Result result = Utils4Tests.deleteAllTestUsers();
		Assert.assertTrue(result.isOk());
		// ... articles
		result = Utils4Tests.deleteAllTestArticles();
		Assert.assertTrue(result.isOk());		
	}

	private void shouldNotBeNullObjects() {
		Assert.assertNotNull(_log);
		Assert.assertNotNull(accessor);
		Assert.assertNotNull(descriptor);
	}	
	
	public static boolean initTestDataStage1User() {
		userMap = Utils4Tests.initTestUsers(1);
		userID = (String) userMap.keySet().toArray()[0];
		CassandraVirtualPath path2Users = new CassandraVirtualPath(
				descriptor,
				Utils4Tests.KSMAINTEST_Users);
		Result result = accessor.update(path2Users, DBUtils.convert2Bytes(userMap));
		
		if(result.isOk())
			System.out.println("Initial user data merged!");
		else{
			System.out.println(result.getResult());
			return false;
		}
		
		path2User = new CassandraVirtualPath(descriptor,
				Utils4Tests.KSMAINTEST_Users + CassandraVirtualPath.PATH_DELIMETER + userID);
		
		return true;
				
	}	
	
	public static boolean initTestDataStage2Articles() {
		articlesMap = Utils4Tests.initTestArticles(articleCount);
		path2UsersIDArticles = new CassandraVirtualPath(
				descriptor,
				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, userID));
		Result result = accessor.update(path2UsersIDArticles, DBUtils
				.convert2Bytes(articlesMap));
		
		if(result.isOk())
			System.out.println("Initial linked articles data merged!");
		else{
			System.out.println(result.getResult());
			return false;
		}
		
		path2UsersIDArticles = new CassandraVirtualPath(
				descriptor,
				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, userID));
		
		return true;
	}
	
	static Map<String, Map<String, String>> userMap = null;
	static Map<String, Map<String, String>> articlesMap = null;
	static CassandraVirtualPath path2User = null;
	static CassandraVirtualPath path2UsersIDArticles = null;
	static int articleCount = 10;
	
}
