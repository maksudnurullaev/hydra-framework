package org.hydra.managers;

import org.jasypt.util.password.StrongPasswordEncryptor;

public final class CryptoManager { // NO_UCD
	private static final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
	
	public static String encryptPassword(String inPassword){ // NO_UCD
		return passwordEncryptor.encryptPassword(inPassword);
	}
	
	public static boolean checkPassword(String inPassword, String inEncryptedPassword){ // NO_UCD
		return passwordEncryptor.checkPassword(inPassword, inEncryptedPassword);
	}
}
