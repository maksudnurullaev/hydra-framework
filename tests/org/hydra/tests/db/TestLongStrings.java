package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Map;

import org.hydra.utils.DB;
import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.QUERY_TYPE;
import org.junit.Assert;
import org.junit.Test;

public class TestLongStrings {
	static String testObjectName = "test_object";
	static String longStringFieldName = "long string"; 
	
	@Test
	public void test_long_string_integrity(){
		String longString = makeLongString(1024);
		
		Map<String,String> data = new HashMap<String, String>();
		data.put("_object", testObjectName);
		data.put(longStringFieldName, longString);
		
		DBUtils.validateData(QUERY_TYPE.INSERT, data);

		String id = data.get("_key");
		Assert.assertTrue(DB.setObject(QUERY_TYPE.INSERT, data));
		
		data.clear(); data.put("_key", id);
		Map<String, Map<String, String>> dbObject = DB.getObjects(data);
		String dbString = dbObject.get(id).get(longStringFieldName);
		Assert.assertTrue(dbString.equals(longString));
	}
	
	static String makeLongString(int size){
		StringBuffer sb = new StringBuffer(size);
		for(int i=0 ; i<size ; i++){
			sb.append(" " + i);
		}
		return(sb.toString());
	}
}
