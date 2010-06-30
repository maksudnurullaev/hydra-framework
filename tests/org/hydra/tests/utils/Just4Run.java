package org.hydra.tests.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.junit.Assert;

public class Just4Run {

	public static void main(String[] args) {
//		CassandraAccessorBean accessor = Utils4Tests.getAccessor();
//		CassandraDescriptorBean descriptor = Utils4Tests.getDescriptor();
//		
//		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, 
//				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, "user0"));
//		
////		// init single user for test
////		Map<String, Map<String, String>> user = Utils4Tests.initTestUsers(1);
////		CassandraVirtualPath path = new CassandraVirtualPath(Utils4Tests.getDescriptor(),
////				Utils4Tests.KSMAINTEST_Users);		
////		Result result = Utils4Tests.getAccessor().update(path, DBUtils.convert2Bytes(user));				
////		Assert.assertTrue(result.isOk());
////		
////		result = Utils4Tests.getAccessor().delete(path);		
////		Assert.assertTrue(result.isOk());
		

		String inKeyID = "inKey";
		String inCfName = "inCfName";
		
		Map<String, Map<String, List<String>>>	inResult = new HashMap<String, Map<String,List<String>>>();
		
		List<String> listOfString12 = new ArrayList<String>();
		listOfString12.add("testValue1");
		listOfString12.add("testValue2");
		
		List<String> listOfString34 = new ArrayList<String>();
		listOfString34.add("testValue3");
		listOfString34.add("testValue4");
		
		Map<String, List<String>> mapOfList12 = new HashMap<String, List<String>>();
		System.out.println("// before");
		mapOfList12.put(inCfName, listOfString12);
		
		DBUtils.joinResults(inCfName, inKeyID, listOfString12, inResult);
		printResult(inResult); 

		Map<String, List<String>> mapOfList34 = new HashMap<String, List<String>>();
		
		mapOfList34.put(inCfName, listOfString34);
		
		// result.put(inKeyID, mapOfList34);
		DBUtils.joinResults(inCfName, inKeyID, listOfString34, inResult);
		
		System.out.println("// after");
		printResult(inResult);		
	}

	private static void printResult(
			Map<String, Map<String, List<String>>> result) {
		for(Map.Entry<String, Map<String, List<String>>> entry:result.entrySet()){
			System.out.println(entry.getKey());
			for(Map.Entry<String, List<String>> subEntry: entry.getValue().entrySet()){
				System.out.println(" ... " + subEntry.getKey());
				for(String string:subEntry.getValue()){
					System.out.println(" ... ... " + string);
				}
			}
		}
	}

}