package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Map;

import org.hydra.utils.DB;
import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.QUERY_TYPE;
import org.junit.Assert;
import org.junit.Test;

public class TestDBSetGetDelete1 {

	@Test
	public void test_set(){
		// set
		String objectId;
		Map<String, String> data = new HashMap<String, String>();
		data.put("_object", "User");
		data.put("version", "Test Version1");
		data.put("name1", "Value1");
		data.put("name2", "Value2");
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.INSERT, data));
		Assert.assertTrue(data.containsKey("_key"));
		Assert.assertTrue(DB.setObject(QUERY_TYPE.INSERT, data));
		objectId = data.get("_key");
		
		// get
		data.clear();
		Assert.assertFalse(DBUtils.validateData(QUERY_TYPE.SELECT, data));
		data.put("_key", objectId);
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.SELECT, data));
		Map<String, Map<String, String>> map = DB.getObject(data);
		Assert.assertTrue(map.containsKey(objectId));
		Assert.assertTrue(map.get(objectId).containsKey("name1"));
		Assert.assertTrue(map.get(objectId).get("name1").contains("Value1"));
		
		// set - update
		data.clear();
		data.put("_key", objectId);
		data.put("name1", "11_value");
		data.put("name3", "value3");
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.UPDATE, data));
		Assert.assertTrue(DB.setObject(QUERY_TYPE.UPDATE, data));
		
		data.clear();
		data.put("_key", objectId);
		map = DB.getObject(data);
		Assert.assertTrue(map.get(objectId).get("name1").contains("11_value"));
		Assert.assertTrue(map.get(objectId).get("name3").contains("value3"));
		
		// delete
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.DELETE, data));	
		Assert.assertTrue(DB.deleteObject(data));
		Assert.assertNull(DB.getObject(data));
		
	}
		
}
