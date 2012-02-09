package org.hydra.tests.urls;

import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.Assert;


public class TestUrls {

	@Test
	public void test_1() {
		Pattern p = Pattern.compile(".*127\\.0\\.0\\.1.*");
		Pattern p2 = Pattern.compile(".*(www\\.)?hydra\\.uz.*");
		Pattern p3 = Pattern.compile(".*(www\\.)?sktour\\.uz.*");
		Test("http://127.0.0.1",        p, true);
		Test("https://127.0.0.1",       p, true);
		Test("https://127.0.0.1/",      p, true);
		Test("http://127.0.0.1/test",   p, true);
		Test("https://127.0.0.1/tests", p, true);
		Test("http://12.0.0.1",         p, false);
		Test("https://128.0.0.1",       p, false);
		Test("http://127.1.0.1/",       p, false);
		Test("https://127.0.2.1/",      p, false);
		Test("http://127.0.0.2/test",   p, false);
		
		Test("https://www.lenta.ru/tests", p2, false);		
		Test("https://www.hydra.uz/tests", p2, true);		
		Test("http://hydra.uz",            p2, true);		

		Test("lenta.ru",     p2, false);		
		Test("www.hydra.uz", p2, true);		
		Test("hydra.uz",     p2, true);		
		Test("http://sktour.uz",    p3, true);		
	
	}

	private static void Test(String string, Pattern p, boolean expected) {
		Assert.assertTrue(p.matcher(string).matches() == expected);
	}
}