package org.hydra.tests.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestVirtualPath {
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
	
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = Utils4Tests.getBeanFactory();
	CassandraDescriptorBean cassandraDescriptorBean = null;

	@Before
	public void test_main() {
		Assert.assertNotNull(factory);

		cassandraDescriptorBean = (CassandraDescriptorBean) factory
				.getBean(Constants._beans_cassandra_descriptor);
		Assert.assertNotNull(cassandraDescriptorBean);
	}

	@Test
	public void test_1_invalid_virtual_path1() {
		CassandraVirtualPath testPath = new CassandraVirtualPath(null, null);
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_DESCRIPTOR);

		testPath = new CassandraVirtualPath(null, INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_DESCRIPTOR);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean, null);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_PATH);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean, INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_PATH_STRUCTURE);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				INVALID_PATH_KSMAINTEST_ARTICLES_ID_TITLE_UUU);
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_PATH_STRUCTURE);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				INVALID_PATH_UNKNOWN_ARTICLES);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_KS);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				INVALID_PATH_KSMAINTEST_UNKNOWN);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_CF);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				INVALID_PATH_KSMAINTEST_ARTICELES_ID_UNKNOWN);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_COLUMN);
	}

	@Test
	public void test_2_valid_virtual_path() {
		CassandraVirtualPath testPath = new CassandraVirtualPath(
				cassandraDescriptorBean, VALID_PATH_KSMAINTEST_USERS);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), USERS);
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_COLUMNS);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___COLUMNS);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), USERS);
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_USERID);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___ID);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), USERS);
		Assert.assertEquals(testPath.getPathPart(PARTS.SUPER), USER_ID);
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_USERID_PASSWORD);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___ID___COL);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), USERS);
		Assert.assertEquals(testPath.getPathPart(PARTS.SUPER), USER_ID);
		Assert.assertEquals(testPath.getPathPart(PARTS.COL), PASSWORD);
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);
		Assert.assertTrue(testPath.colBean != null && testPath.colBean instanceof ColumnBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_LINKS);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___LINKS);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), USERS);
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___ID___LINKS);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), INVALID_PATH_KSMAINTEST);
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), USERS);
		Assert.assertEquals(testPath.getPathPart(PARTS.SUPER), USER_ID);
		Assert.assertEquals(testPath.getPathPart(PARTS.COL), "Articles");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);
		Assert.assertTrue(testPath.colBean != null && testPath.colBean instanceof ColumnBean);
	}
}
