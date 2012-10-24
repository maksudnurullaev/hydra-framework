package org.hydra.tests.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hydra.utils.DB;
import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.QUERY_TYPE;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDBSetGetDeleteMany {
	static List<String> ids = new ArrayList<String>();
	static int record_count = 1000;
	static String testObjectName = "test_object";
	
	@BeforeClass
	public static void init_me(){
		// delete old test objects
		deleteTestObjects();
		// create new test objects
		createTestObjects();
	}

	@AfterClass
	public static void finish_me(){
		deleteTestObjects();
	}

	private static void createTestObjects() {
		Map<String, String> data = new HashMap<String, String>();			
		for(int i=0 ; i<record_count ; i++){
			data.clear();
			data.put("_object", testObjectName);
			data.put("testField1", "testValue" + i);
			data.put("testField2", "testValue" + (i%2 == 0?0:(i%2)));
			data.put("testField4", "testValue" + (i%4 == 0?0:(i%4)));
			DBUtils.validateData(QUERY_TYPE.INSERT, data);
			Assert.assertNotNull(data.get("_key"));
			ids.add(data.get("_key"));
			Assert.assertTrue(DB.setObject(QUERY_TYPE.INSERT, data));
		}
	}

	
	private static void deleteTestObjects() {
		Connection connection = DBUtils.getConnection();
		try {
			Statement statement = connection.createStatement();
			statement.execute("DELETE FROM OBJECTS WHERE KEY LIKE '" + testObjectName.toLowerCase() + "%';");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_count(){
		Map<String, String> data = new HashMap<String, String>();
		data.put("_object", testObjectName);
		Assert.assertTrue(DBUtils.validateData(QUERY_TYPE.SELECT_COUNT, data));
		int count = DB.getCountOfObjects(data);
		Assert.assertTrue(count == record_count);
	}
	
	@Test
	public void test_find(){
		Map<String, String> data = new HashMap<String, String>();
		data.put("_object", testObjectName);
		data.put("testField1", "='testValue1'");
		Map<String, Map<String, String>> map = DB.getObjects(data);
		Assert.assertTrue(map.size() == 1);
		
	}
		
	@Test
	public void test_find_like(){
		Map<String, String> data = new HashMap<String, String>();
		data.put("_object", testObjectName);
		data.put("testField1", " LIKE 'testValue99%' ");
		Map<String, Map<String, String>> map = DB.getObjects(data);
		Assert.assertTrue(map.size() == 11);		
	}
	
	@Test
	public void test_find_ext(){
		Map<String, String> data = new HashMap<String, String>();
		data.put("_object", testObjectName);
		data.put("testField1", " LIKE 'testValue99%' ");
		data.put("testField2", " = 'testValue0' ");
		Map<String, Map<String, String>> map = DB.getObjects(data);
		map = DBUtils.sortMapByFoundFields(map, 2); 
		Assert.assertTrue(map.size() == 5);		
	}
	
	@Test
	public void test_pagination(){
		Map<String, String> data = new HashMap<String, String>();
		data.put("_object", testObjectName);
		Map<String, Map<String, String>> map = DB.getObjects(data);
		Assert.assertTrue(map.size() == record_count);			
		Map<String, List<String>> pages = DBUtils.sortMapByPages(DBUtils.getKeysAsList(map), 50); 
		Assert.assertTrue(pages.size() == 20);		
		pages = DBUtils.sortMapByPages(DBUtils.getKeysAsList(map), 1); 
		Assert.assertTrue(pages.size() == record_count);		
		pages = DBUtils.sortMapByPages(DBUtils.getKeysAsList(map), 5000); 
		Assert.assertTrue(pages.size() == 1);		
		
		int remove_count = 46;
		for(int i=(record_count - remove_count) ; i<record_count ; i++){
			map.remove(ids.toArray()[i]);
		}
		pages = DBUtils.sortMapByPages(DBUtils.getKeysAsList(map), 50); 
		Assert.assertTrue(pages.size() == 20);		
		Assert.assertTrue(pages.get("20").size() == 4);
	}
}
