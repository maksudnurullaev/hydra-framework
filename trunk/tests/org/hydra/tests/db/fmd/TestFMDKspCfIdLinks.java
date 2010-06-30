package org.hydra.tests.db.fmd;

import java.util.Map;

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
	 * FUD - (Find, Update, Delete)
	 */
	static Map<String, Map<String, String>> testUsersMap = null;

	Log _log = LogFactory.getLog(this.getClass());
	static CassandraAccessorBean accessor = Utils4Tests.getAccessor();
	static CassandraDescriptorBean descriptor = Utils4Tests.getDescriptor();

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
	public void test_1_user_with_2_articles() {
		int articleCount = 10;
		
		// init single user for test
		Map<String, Map<String, String>> user = Utils4Tests.initTestUsers(1);
		String userID = (String) user.keySet().toArray()[0];
		CassandraVirtualPath path2Users = new CassandraVirtualPath(descriptor,
				Utils4Tests.KSMAINTEST_Users);
		
		Result result = accessor
				.update(path2Users, DBUtils.convert2Bytes(user));
		// ... test result
		Assert.assertTrue(result.isOk());

		// FIND links (initial count)
		CassandraVirtualPath path2UsersIDArticles = new CassandraVirtualPath(
				descriptor,
				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, userID));
		ResultAsListOfColumnOrSuperColumn dbResult = accessor
				.find(path2UsersIDArticles);
		Assert.assertTrue(dbResult.isOk());
		int initialArticleCount = dbResult.getColumnOrSuperColumn().size();

		// UPDATE/INSERT (insert 2 articles for users)
		Map<String, Map<String, String>> articles = Utils4Tests
				.initTestArticles(articleCount);
		result = accessor.update(path2UsersIDArticles, DBUtils
				.convert2Bytes(articles));
		Assert.assertTrue(result.isOk());

		// TODO UPDATE/CHANGE user's articles

		// FIND links(all new articles)
		dbResult = accessor.find(path2UsersIDArticles);
		Assert.assertTrue(dbResult.isOk());
		Assert.assertEquals(1, dbResult.getColumnOrSuperColumn().size());
		Assert.assertEquals(articleCount, dbResult.getColumnOrSuperColumn()
				.get(0).super_column.columns.size());

		// [debug only] DBUtils.printResult(dbResult);

		// TODO Check cascade deletion one by one records
		// DELETE(delete links)
		Assert.assertEquals(String.format(
				Utils4Tests.KSMAINTEST_Users_S_Articles, userID),
				path2UsersIDArticles.getPath());
		Assert.assertEquals(PATH_TYPE.KSP___CF___ID___LINKNAME,
				path2UsersIDArticles.getPathType());

		result = accessor.delete(path2UsersIDArticles);

		// ... test deletes
		dbResult = accessor.find(path2UsersIDArticles);
		Assert.assertTrue(dbResult.isOk());
		Assert.assertEquals(initialArticleCount, dbResult
				.getColumnOrSuperColumn().size());
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
}
