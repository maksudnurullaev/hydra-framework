package org.hydra.tests.db.beans;

import java.util.HashMap;
import java.util.Map;

import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.tests.bean.Test0;
import org.hydra.tests.db.path.TestVirtualPath;
import org.hydra.utils.DBUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestValidateFields extends Test0 {
	@Test
	public void test_1_simple() {
		// 1. setup fields
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap.put("Text", "Some text");
		// 2. setup cf's
		CassandraVirtualPath path = 
			new CassandraVirtualPath(_cassandraDescriptor, 
					TestVirtualPath.VALID_PATH_KSMAINTEST_USERS_USERID_ARTICLES);
		// test against users cf
		Assert.assertFalse(DBUtils.validateFields(path._cfBean, fieldValueMap));
		// test against articles cf
		Assert.assertTrue(DBUtils.validateFields(path._cfLinkBean, fieldValueMap));
	}
}
