package org.hydra.tests.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hydra.utils.DBObjects;
import org.junit.Before;
import org.junit.Test;

public class TestDBFind {

	List<Map<String, String>> objects = new ArrayList<Map<String,String>>();
	List<String> objectIds= new ArrayList<String>();
	int objects_count3 = 1000;

	@Before
	public void init_me(){
		for(int i=0; i<objects_count3 ; i++){
			Map<String, String> obj = new HashMap<String, String>();
			obj.put("_object_name", "Object" + objects_count3);
			obj.put("version", "Some Version");
//			obj.put("key" + i, "Value" + i);
			objects.add(obj);	
		}
		DBObjects.setObjects("Test", objects, objectIds);
		System.out.println("Inserted objects count: " + objectIds.size());
	}
		
	@Test
	public void test_new_objects_existance(){
	}
	
//	@After
//	public void finish(){
//		//Assert.assertTrue(DBUtils.deleteKeys("Test", "Objects", objectIds) == ERROR_CODES.NO_ERROR);
//	}
}
