package org.hydra.tests.utils;

import org.hydra.utils.DBUtils;

public class Just4Test2 {
	
	public static void main(String[] argv){
		for (int i = 0; i < 10; i++) {
			String objName = "Object";
			System.out.println(objName + i  + ": " + DBUtils.GetDBObjectID(objName + i));			
		}
	}
}
