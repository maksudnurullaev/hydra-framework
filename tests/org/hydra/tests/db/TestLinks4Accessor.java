package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.hydra.utils.CryptoManager;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestLinks4Accessor {
	static final String VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES = "KSMainTEST.Users.userID.Articles";
	static final String VALID_PATH_KSMAINTEST_USERS_LINKS = "KSMainTEST.Users.LINKS";
	static final String VALID_PATH_KSMAINTEST_USERS_USERID_PASSWORD = "KSMainTEST.Users.userID.Password";
	static final String VALID_PATH_KSMAINTEST_USERS_USERID = "KSMainTEST.Users.userID";
	static final String VALID_PATH_KSMAINTEST_USERS_COLUMNS = "KSMainTEST.Users.COLUMNS";
	static final String VALID_PATH_KSMAINTEST_USERS = "KSMainTEST.Users";
	
	static final String INVALID_PATH_KSMAINTEST_ARTICELES_ID_UNKNOWN = "KSMainTEST.Articles.ID.UnkownColumn";
	static final String INVALID_PATH_KSMAINTEST_UNKNOWN = "KSMainTEST.UknownArticles";
	static final String INVALID_PATH_UNKNOWN_ARTICLES = "UnknownKSMain.Articles";
	static final String INVALID_PATH_KSMAINTEST = "KSMainTEST";
	static final String INVALID_PATH_KSMAINTEST_ARTICLES_ID_TITLE_UUU = "KSMainTEST.Articles.ID.Title.Extended";
	
	static final String USERS = "Users";
	static final String PASSWORD = "Password";
	static final String USER_ID = "userID";
	static final String ARICLE_ID = "articleID";
	private static final String EMAIL = "Email";
	
	
	private static final String TEST_S_MAIL_COM = "test@mail.com";
	
	static Map<String, Map<String, String>> testUserMap = new HashMap<String, Map<String, String>>();
	static Map<String, Map<String, String>> testArticleMap = new HashMap<String, Map<String, String>>();
	
	Log _log = LogFactory.getLog(this.getClass());
	static BeanFactory beanFactory = Utils4Tests.getBeanFactory();
	static CassandraAccessorBean accessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
	static CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
	
	@BeforeClass
	public static void generateTestData(){
		// 0. setup accessor
		if(!accessor.isValid())
			accessor.setup();	
		// clear old data
		clearTestData();		
		// 1. init test data
		generateTestUser();
		generateTestUserLinks();
	}

	private static void generateTestUserLinks() {
		// 1. setup fields
		Map<String, String> articleObjectMap = new HashMap<String, String>();
		articleObjectMap.put("Title", "Title text");
		articleObjectMap.put("Text", "Article text");
		// 2. setup article map
		testArticleMap.put(ARICLE_ID, articleObjectMap);
		// 3. setup access path
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(path.getPathType(), PATH_TYPE.KSP___CF___ID___LINKNAME);
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		// 4. insert data
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(testArticleMap));
		// 5. test result
		Assert.assertTrue(batchInsertResult.isOk());
	}

	private static void generateTestUser() {
		// 1. setup fields
		Map<String, String> userObjectMap = new HashMap<String, String>();
		userObjectMap.put(PASSWORD, CryptoManager.encryptPassword(USER_ID));
		userObjectMap.put(EMAIL, TEST_S_MAIL_COM);
		// 2. setup user map
		testUserMap.put(USER_ID, userObjectMap);
		// 3. setup access path
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertEquals(path.getPathType(), PATH_TYPE.KSP___CF);
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		// 4. insert data to db
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(testUserMap));
		// 5. Test result
		Assert.assertTrue(batchInsertResult.isOk());
	}

	@AfterClass
	public static void clearTestData() {
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		
		accessor.batchDelete4KspCf(path);
	}
	
	@Before
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(_log);
		Assert.assertNotNull(accessor);
		Assert.assertNotNull(descriptor);		
	}
	
	@Test
	public void test_1_links(){
		// get users from database
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, VALID_PATH_KSMAINTEST_USERS);
		ResultAsListOfColumnOrSuperColumn result = accessor.resultAsListOfColumns4KspCf(testPath);
		// test result
		Assert.assertTrue(result.isOk());
		// test compare resultMap & result
		
		for(ColumnOrSuperColumn columnOrSuperColumn:result.getColumnOrSuperColumn()){
			String ID1 = DBUtils.bytes2UTF8String(columnOrSuperColumn.super_column.name);
			Assert.assertTrue(testUserMap.containsKey(ID1));
			Map<String, String> subMap = testUserMap.get(ID1);
			// test column and values
			Assert.assertNotNull(columnOrSuperColumn.super_column.getColumns());
			for(Column column: columnOrSuperColumn.super_column.getColumns()){
				String ID2 = DBUtils.bytes2UTF8String(column.name);
				Assert.assertTrue(subMap.containsKey(ID2));
				Assert.assertEquals(subMap.get(ID2), DBUtils.bytes2UTF8String(column.value));
			}
			Map<String, byte[]> mapStringByteA = DBUtils.converMapStringByteA(columnOrSuperColumn.super_column.getColumns());
			Assert.assertNotNull(mapStringByteA.get(PASSWORD));
			Assert.assertTrue(CryptoManager.checkPassword(ID1, DBUtils.bytes2UTF8String(mapStringByteA.get(PASSWORD))));
		}
		// print result
		// DBUtils.printResult(result);
	}
}
