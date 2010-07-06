package org.hydra.tests.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestVirtualPath {
	public static final String VALID_PATH_KSMAINTEST_USERS = "KSMainTEST--->Users";
	public static final String VALID_PATH_KSMAINTEST_USERS_USERID  = "KSMainTEST--->Users--->userID";
	public static final String VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES = "KSMainTEST--->Users--->userID--->Articles";
	public static final String VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES_ARTICLEID = "KSMainTEST--->Users--->userID--->Articles--->articleID";
	
	public static final String INVALID_PATH_KSMAINTEST = "KSMainTEST";
	public static final String INVALID_PATH_UNKNOWN_ARTICLES = "UnknownKSMain--->Articles";
	public static final String INVALID_PATH_KSMAINTEST_UNKNOWN = "KSMainTEST--->UknownArticles";
	public static final String INVALID_PATH_KSMAINTEST_ARTICELES_ID_UNKNOWN = "KSMainTEST--->Articles--->ID--->UnkownColumn";
	public static final String INVALID_PATH_KSMAINTEST_ARTICLES_ID_TITLE_UUU_XXX = "KSMainTEST--->Articles--->ID--->Title--->Extended--->Field";
	
	public static final String KSP = "KSMainTEST";
	public static final String CF = "Users";
	public static final String COL = "Password";
	public static final String ID = "userID";
	public static final String LINKNAME = "Articles";
	public static final String LINKID = "articleID";
	
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory factory = BeansUtils.getBeanFactory();
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
				INVALID_PATH_KSMAINTEST_ARTICLES_ID_TITLE_UUU_XXX);
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
				PATH_TYPE.KSP___CF);
		Assert.assertEquals(testPath.getPathPart(PARTS.P1_KSP), KSP);
		Assert.assertEquals(testPath.getPathPart(PARTS.P2_CF), CF);
		Assert.assertTrue(testPath._kspBean != null && testPath._kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath._cfBean != null && testPath._cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_USERID);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___ID);
		Assert.assertEquals(testPath.getPathPart(PARTS.P1_KSP), KSP);
		Assert.assertEquals(testPath.getPathPart(PARTS.P2_CF), CF);
		Assert.assertEquals(testPath.getPathPart(PARTS.P3_KEY), ID);
		Assert.assertTrue(testPath._kspBean != null && testPath._kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath._cfBean != null && testPath._cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		Assert.assertEquals(PATH_TYPE.KSP___CF___ID___LINKNAME,
				testPath.getPathType());
		Assert.assertEquals(testPath.getPathPart(PARTS.P1_KSP), KSP);
		Assert.assertEquals(testPath.getPathPart(PARTS.P2_CF), CF);
		Assert.assertEquals(testPath.getPathPart(PARTS.P3_KEY), ID);
		Assert.assertEquals(testPath.getPathPart(PARTS.P4_SUPER), "Articles");
		Assert.assertTrue(testPath._kspBean != null && testPath._kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath._cfBean != null && testPath._cfBean instanceof ColumnFamilyBean);
		Assert.assertTrue(testPath._cfLinkBean != null && testPath._cfBean instanceof ColumnFamilyBean);
		
		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES_ARTICLEID);
		Assert.assertEquals(testPath.getPathType(),
				PATH_TYPE.KSP___CF___ID___LINKNAME__LINKID);
		Assert.assertEquals(testPath.getPathPart(PARTS.P1_KSP), KSP);
		Assert.assertEquals(testPath.getPathPart(PARTS.P2_CF), CF);
		Assert.assertEquals(testPath.getPathPart(PARTS.P3_KEY), ID);
		Assert.assertEquals(testPath.getPathPart(PARTS.P4_SUPER), "Articles");
		Assert.assertTrue(testPath._kspBean != null && testPath._kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath._cfBean != null && testPath._cfBean instanceof ColumnFamilyBean);
		Assert.assertEquals(LINKNAME, testPath.getLinkName());
		Assert.assertEquals(LINKID, testPath.getLinkID());
		
	}
}
