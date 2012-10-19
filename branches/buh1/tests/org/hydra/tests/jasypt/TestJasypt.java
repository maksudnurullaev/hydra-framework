package org.hydra.tests.jasypt;

import org.hydra.managers.CryptoManager;
import org.junit.Assert;
import org.junit.Test;

public class TestJasypt {
	
	@Test
	public void test_basic() {
		String password = "Strong Password";
		String encryptedPassword = CryptoManager.encryptPassword(password);	
		System.out.println(encryptedPassword);
		Assert.assertTrue(CryptoManager.checkPassword(password, encryptedPassword));
	}
}
