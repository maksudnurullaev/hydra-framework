package org.hydra.tests.utils;

public class Just4Test2 {

	public static void main(String[] argv){
		String str = "</textarea> . --- </texTArea>";
		str = str.replaceAll("\\W", "_");

		System.out.println("After replacement:\n" + "   " + str);

	}
}
