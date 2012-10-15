package org.hydra.tests.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hydra.utils.DBUtils;
import org.hydra.utils.ErrorUtils.ERROR_CODES;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDBStage1 {

	List<Map<String, String>> objects = new ArrayList<Map<String,String>>();
	List<String> objectIds= new ArrayList<String>();

	@Before
	public void init_me(){				
		Map<String, String> obj1 = new HashMap<String, String>();
		obj1.put("_object_name", "User");
		obj1.put("_version", "Test Version1");
		obj1.put("key11", "Value11");
		obj1.put("key12", "Value12");		
		objects.add(obj1);
		
		Map<String, String> obj2 = new HashMap<String, String>();
		obj2.put("_object_name", "User");
		obj2.put("_version", "Test Version2");
		obj2.put("key21", "Value11");
		obj2.put("key22", "Value12");		
		objects.add(obj2);

		Map<String, String> obj_invalid = new HashMap<String, String>();
		obj_invalid.put("_object_name", "User");
		objects.add(obj_invalid);
	
		DBUtils.setObjects("Test", objects, objectIds);
		
		Assert.assertTrue(2 == DBUtils.getCountOf("Test", "Objects"));
	}
		
	@Test
	public void test_new_objects_existance(){
		Assert.assertTrue(objectIds.size() == 2);
		for(String id:objectIds){
			Map<String, String> result = DBUtils.getColumnsAsMap("Test", "Objects", id);
			if(result.containsKey("key11")){
				Assert.assertTrue(result.containsKey("key12"));
				Assert.assertFalse(result.containsKey("key21"));
			} else {
				Assert.assertTrue(result.containsKey("key21"));
				Assert.assertFalse(result.containsKey("key12"));				
			}
		}
	}
	
	@Test
	public void test_scope_of_objects(){
		Map<String, Map<String, String>> result = DBUtils.getObjectsAsMap("Test", objectIds);
		for(String id:objectIds){
			Assert.assertTrue(result.containsKey(id));
		}
	}
	
	@After
	public void finish(){
		for(String id:objectIds){
			ERROR_CODES result = DBUtils.deleteKey("Test", "Objects", id);
			Assert.assertTrue(result == ERROR_CODES.NO_ERROR);
			Assert.assertNull(DBUtils.getColumnsAsMap("Test", "Objects", id));
		}
	}
}
