package org.hydra.tests.utils;

import java.util.regex.Pattern;


public class Just4Run {

	
	public static void main(String[] args) {
//		Pattern p = Pattern.compile("http://127.0.0.1:8181");
//		Test("http://127.0.0.1", p);
		Pattern p = Pattern.compile("^https?://127\\.0\\.0\\.1.*");
		Pattern p2 = Pattern.compile("^https?://(www\\.)?hydra\\.uz.*");
		Test("http://127.0.0.1", p);
		Test("https://127.0.0.1", p);
		Test("http://127.0.0.1/", p);
		Test("https://127.0.0.1/", p);
		Test("http://127.0.0.1/test", p);
		Test("https://127.0.0.1/tests", p);
		Test("http://12.0.0.1", p);
		Test("https://128.0.0.1", p);
		Test("http://127.1.0.1/", p);
		Test("https://127.0.2.1/", p);
		Test("http://127.0.0.2/test", p);
		Test("https://www.lenta.ru/tests", p);		
		Test("https://www.hydra.uz/tests", p2);		
		Test("http://hydra.uz", p2);		
	}

	private static void Test(String string, Pattern p) {
		if(p.matcher(string).matches())
			System.out.println(string + ": Yes");
		else
			System.out.println(string + ": No");
	}
}