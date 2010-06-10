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
import org.hydra.tests.bean.TestVirtualPath;
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
	private static final String TEST_MAIL_COM = "test@mail.com";
	private static final String PASSWORD = "Password";
	private static final String EMAIL = "Email";
	private static final String ARTICLEID = "articleID";
	
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
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap.put("Title", "Title text");
		fieldValueMap.put("Text", "Article text");
		// 2. setup article map
		testArticleMap.put(ARTICLEID, fieldValueMap);
				
		// 3. setup access path
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(path.getPathType(), PATH_TYPE.KSP___CF___ID___LINKNAME);
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		// 3. validate columns
		Assert.assertNotNull(path._linkCf);
		Assert.assertTrue(DBUtils.validateCfAndMap(path._linkCf, fieldValueMap));
		// 4. insert data
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(testArticleMap));
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
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertEquals(path.getPathType(), PATH_TYPE.KSP___CF);
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		Assert.assertTrue(DBUtils.validateCfAndMap(path._cfBean, fieldValueMap));
		// 4. insert data to db
		Result batchInsertResult = accessor.batchMutate(path, DBUtils.convertMapByteAMapByteAByteA(testUserMap));
		// 5. Test result
		Assert.assertTrue(batchInsertResult.isOk());
	}

	@AfterClass
	public static void clearTestData() {
		// delete all users
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		accessor.delete4KspCf(path);
		
		// delete all articles
		path = new CassandraVirtualPath(descriptor, "KSMainTEST.Articles");
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		accessor.delete4KspCf(path);
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
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, TestVirtualPath.VALID_PATH_KSMAINTEST_USERS);
		ResultAsListOfColumnOrSuperColumn result = accessor.get4KspCf(testPath);
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
