package org.hydra.tests.jasypt;

import org.hydra.utils.CryptoManager;
import org.junit.Assert;
import org.junit.Test;

public class TestJasypt {
	
	@Test
	public void test_basic() {
		String password = "Strong password";
		String encryptedPassword = CryptoManager.encryptPassword(password);		
		Assert.assertTrue(CryptoManager.checkPassword(password, encryptedPassword));
	}
}
