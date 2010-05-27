package org.hydra.tests.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
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
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_DESCRIPTOR);
		
		testPath = new CassandraVirtualPath(null, "KSMain");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_DESCRIPTOR);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean, null);
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_PATH);

		testPath = new CassandraVirtualPath(
				cassandraDescriptorBean, "KSMain");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_PATH_STRUCTURE);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean,
				"KSMain.Articles.ID.Title.Extended");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_PATH_STRUCTURE);

		testPath = new CassandraVirtualPath(cassandraDescriptorBean, 
				"UnknownKSMain.Articles");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_KS);
		
		testPath = new CassandraVirtualPath(cassandraDescriptorBean, 
			"KSMain.UknownArticles");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_CF);
		
		testPath = new CassandraVirtualPath(cassandraDescriptorBean, 
			"KSMain.Articles.ID.UnkownColumn");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.INVALID_COLUMN);
	}

	@Test
	public void test_2_valid_virtual_path() {
		CassandraVirtualPath testPath = new CassandraVirtualPath(
				cassandraDescriptorBean, "KSMain.Articles");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(), RESULT_TYPES.DATA_KSP_CF);

		testPath = new CassandraVirtualPath(
				cassandraDescriptorBean, "KSMain.Articles.ID");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(), RESULT_TYPES.DATA_KSP_CF_COLUMNS_SUPER);

		testPath = new CassandraVirtualPath(
				cassandraDescriptorBean, "KSMain.Articles.ID.Title");
		Assert.assertEquals(testPath.getErrorCode(), ERR_CODES.NO_ERROR);
		Assert.assertEquals(testPath.getResultType(), RESULT_TYPES.DATA_KSP_CF_COLUMNS_SUPER_COLUMN);
	}

}
