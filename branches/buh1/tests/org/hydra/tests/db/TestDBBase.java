package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Map;

import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.QUERY_TYPE;
import org.junit.Assert;
import org.junit.Test;

public class TestDBBase {

	@Test
	public void test_validation_with_new_key(){
		Assert.assertFalse(DBUtils.validateData(QUERY_TYPE.INSERT, null));
		Map<String, String> data = new HashMap<String, String>();
		Assert.assertFalse(DBUtils.validateData(QUERY_TYPE.INSERT, data));
		data.put("name", "Test name field value");
		Assert.assertFalse(DBUtils.validateData(QUERY_TYPE.INSERT, data));
		data.put("_object", "VerSion");
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.INSERT, data));
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.UPDATE, data));
	}

	@Test
	public void test_validation_with_existance_key(){
		String key = "Test key";
		Map<String, String> data = new HashMap<String, String>();

		data.put("_key", key);
		Assert.assertFalse(DBUtils.validateData(QUERY_TYPE.INSERT,data));
		
		data.put("_object", "VerSion");
		DBUtils.validateData(QUERY_TYPE.INSERT, data);
		Assert.assertTrue(key.equals(data.get("_key")));
		Assert.assertFalse(DBUtils.validateData(QUERY_TYPE.INSERT, data));
		
		data.put("name", "Some Value");
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.UPDATE, data));		
		
		data.remove("_key");
		data.put("_object", "Some Object");
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.INSERT, data));
		Assert.assertTrue(data.containsKey("_key"));
	}
}
