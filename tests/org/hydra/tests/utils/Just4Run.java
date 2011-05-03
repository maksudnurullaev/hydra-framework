package org.hydra.tests.utils;

import org.hydra.utils.Moder;
import org.hydra.utils.Utils;


public class Just4Run {
	
	static String content = "[[DB|Template|html.body.top|html]]\n" +
	                		"[[DB|Template|html.body.middle|html]]\n" +
	                		"[[DB|Template|html.body.foot|html]]";

	public static void main(String args[]) {
		
		String inLocale = "eng";
		String inUserID = "testUserId";
		Moder inModer = new Moder(null);
		
		String resString = Utils.deployContent(content, "HydraUz", inLocale , inUserID , inModer );
		
		System.out.println(resString);
	}
	
}