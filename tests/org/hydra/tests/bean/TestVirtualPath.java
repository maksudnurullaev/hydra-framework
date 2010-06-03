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
import org.hydra.db.server.CassandraVirtualPath.RESULT_TYPES;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestVirtualPath {
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

		testPath = new CassandraVirtualPath(null, "KSMainTEST");
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_DESCRIPTOR);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean, null);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_PATH);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean, "KSMainTEST");
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_PATH_STRUCTURE);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Articles.ID.Title.Extended");
		Assert.assertEquals(testPath.getErrorCode(),
				ERR_CODES.INVALID_PATH_STRUCTURE);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"UnknownKSMain.Articles");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_KS);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.UknownArticles");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_CF);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Articles.ID.UnkownColumn");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_COLUMN);
	}

	@Test
	public void test_2_valid_virtual_path() {
		CassandraVirtualPath testPath = new CassandraVirtualPath(
				cassandraDescriptorBean, "KSMainTEST.Users");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(),
				RESULT_TYPES.LIST_OF_IDS4KSP_CF);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), "KSMainTEST");
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), "Users");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Users.COLUMNS");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(),
				RESULT_TYPES.MAP4KSP_CF_COLUMNS);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), "KSMainTEST");
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), "Users");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Users.userID");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(),
				RESULT_TYPES.LIST_OF_COLS4KSP_CF_COLUMNS_SUPER);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), "KSMainTEST");
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), "Users");
		Assert.assertEquals(testPath.getPathPart(PARTS.SUPE_R), "userID");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Users.userID.Password");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(),
				RESULT_TYPES.COL4KSP_CF_COLUMNS_SUPER_COLUMN);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), "KSMainTEST");
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), "Users");
		Assert.assertEquals(testPath.getPathPart(PARTS.SUPE_R), "userID");
		Assert.assertEquals(testPath.getPathPart(PARTS.COL), "Password");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);
		Assert.assertTrue(testPath.colBean != null && testPath.colBean instanceof ColumnBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Users.LINKS");
		Assert.assertEquals(testPath.getResultType(),
				RESULT_TYPES.MAP4KSP_CF_LINKS);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), "KSMainTEST");
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), "Users");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMainTEST.Users.userID.Articles");
		Assert.assertEquals(testPath.getResultType(),
				RESULT_TYPES.LIST_OF_IDS4KSP_CF_LINKS_SUPER_COLUMN);
		Assert.assertEquals(testPath.getPathPart(PARTS.KSP), "KSMainTEST");
		Assert.assertEquals(testPath.getPathPart(PARTS.CF), "Users");
		Assert.assertEquals(testPath.getPathPart(PARTS.SUPE_R), "userID");
		Assert.assertEquals(testPath.getPathPart(PARTS.COL), "Articles");
		Assert.assertTrue(testPath.kspBean != null && testPath.kspBean instanceof KeyspaceBean);
		Assert.assertTrue(testPath.cfBean != null && testPath.cfBean instanceof ColumnFamilyBean);
		Assert.assertTrue(testPath.colBean != null && testPath.colBean instanceof ColumnBean);
	}
}
