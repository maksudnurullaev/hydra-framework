package org.hydra.tests.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestDBSetGetDelete {

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
	
		//TODO DB.setObjects("Test", objects, objectIds);		
	}
	
	@Test
	public void test_set(){
		
	}
	
}
