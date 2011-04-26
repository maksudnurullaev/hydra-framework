package org.hydra.tests.utils;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

public class Just4Run {
	
	public static void main(String args[]) {
		//String inputStr = "ab12 [[DB|Text|testID|div]] \n \t cd efg34[[DB|Text|key|div]] 123";
		//System.out.println(Utils.deployContent(inputStr,"HydraUz", "eng",null));
		//System.out.println(DBUtils.getFromKey("Text", "testID", "HydraUz", "eng"));
		//System.out.println(DBUtils.getFromKey("Text", "testID", "HydraUz", "rus"));
		Object o = BeansUtils4Tests.getBean("cfHydraUzText");
		if(o instanceof SimpleCassandraDao){
			SimpleCassandraDao cf = (SimpleCassandraDao) o;
			System.out.println(cf.get("1", "value"));
		}else{
			System.out.println("ERROR!");
		}
	}
	
}