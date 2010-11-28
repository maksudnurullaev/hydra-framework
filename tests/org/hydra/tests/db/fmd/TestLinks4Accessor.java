package org.hydra.tests.db.fmd;

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.tests.Test0;
import org.hydra.tests.db.path.TestVirtualPath;
import org.hydra.utils.CryptoManager;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLinks4Accessor extends Test0 {
	private static final String KS_MAIN_TEST_ARTICLES = "KSMainTEST--->Articles";
	private static final String TEST_MAIL_COM = "test@mail.com";
	private static final String PASSWORD = "Password";
	private static final String EMAIL = "Email";
	private static final String ARTICLEID = "articleID";
	
	static Map<String, Map<String, String>> testUserMap = new HashMap<String, Map<String, String>>();
	static Map<String, Map<String, String>> testArticleMap = new HashMap<String, Map<String, String>>();
	
	@BeforeClass
	public static void generateTestData(){
		// clear old data
		clearTestData();		
		// 1. init test data
		generateTestUser();
		generateTestUserLinks();
	}

	private static void generateTestUserLinks() {
		// 1. setup fields
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap.put("Title", "Title text");
		fieldValueMap.put("Text", "Article text");
		// 2. setup article map
		testArticleMap.put(ARTICLEID, fieldValueMap);
				
		// 3. setup access path
		CassandraVirtualPath path = new CassandraVirtualPath(_cassandraDescriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(path.getPathType(), PATH_TYPE.KSP___CF___KEY___SUPER);
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		// 3. validate columns
		Assert.assertNotNull(path._cfLinkBean);
		Assert.assertTrue(DBUtils.validateFields(path._cfLinkBean, fieldValueMap));
		// 4. insert data
		Result batchInsertResult = _cassandraAccessor.update(path, DBUtils.convert2Bytes(testArticleMap));
		// 5. test result
		Assert.assertTrue(batchInsertResult.isOk());
	}

	private static void generateTestUser() {
		// 1. setup fields
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap.put(PASSWORD, CryptoManager.encryptPassword(TestVirtualPath.ID));
		fieldValueMap.put(EMAIL, TEST_MAIL_COM);
		// 2. setup user map
		testUserMap.put(TestVirtualPath.ID, fieldValueMap);
		// 3. setup access path
		CassandraVirtualPath path = new CassandraVirtualPath(_cassandraDescriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertEquals(path.getPathType(), PATH_TYPE.KSP___CF);
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(DBUtils.validateFields(path._cfBean, fieldValueMap));
		// 4. insert data to db
		Result batchInsertResult = _cassandraAccessor.update(path, DBUtils.convert2Bytes(testUserMap));
		// 5. Test result
		Assert.assertTrue(batchInsertResult.isOk());
	}

	@AfterClass
	public static void clearTestData() {
		// delete all users
		CassandraVirtualPath path = new CassandraVirtualPath(_cassandraDescriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		_cassandraAccessor.delete(path);
		
		// delete all articles
		path = new CassandraVirtualPath(_cassandraDescriptor, KS_MAIN_TEST_ARTICLES);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		_cassandraAccessor.delete(path);
	}

	@Test
	public void test_1_links(){
		// get users from database
		CassandraVirtualPath testPath = new CassandraVirtualPath(_cassandraDescriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS);
		ResultAsListOfColumnOrSuperColumn result = _cassandraAccessor.find(testPath);
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
