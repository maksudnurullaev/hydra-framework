package org.hydra.tests.utils;

import org.hydra.managers.TextManager;


public class Just4Run {

	public static void main(String args[]) {
		TextManager tm = new TextManager();
		
		System.out.println("html.body.vtop: " + tm.getTemplate("html.body.vtop"));
		System.out.println("MainPage.Title(ENG): " + tm.getTextByKey("MainPage.Title",	null, "eng"));
		System.out.println("MainPage.Title(RUS): " + tm.getTextByKey("MainPage.Title",	null, "rus"));
	}
	
}