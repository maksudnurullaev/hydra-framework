package org.hydra.tests.utils;

public class Just4Test2 {

	public static void main(String[] argv){
		String str = "</textarea> --- </texTArea>";

		String result = str.replaceAll("(?i)</TextArea>", "[[Dictionary|Template|template.textarea.endtag|html]]");
		System.out.println("After replacement:\n" + "   " + result);

	}
}
