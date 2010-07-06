package org.hydra.tests.utils;


public class Just4Run {

	
	public static void main(String[] args) {
		String testString1 = "KSMainTEST--->Users--->3cb92fbd-5ebd-42a7-86d5-05be37c8b91d";
		String testString2 = "KSMainTEST--->Users--->2010.07.06 16:55:37 459 - 7eeca97e-e0d9-4489-959f-bacf8ee26f7d";
		
		String[] test1 = testString1.split("--->");
		String[] test2 = testString2.split("--->");		
		
		for(String string:test1) System.out.println(string);
		for(String string:test2) System.out.println(string);		
		
	}

}