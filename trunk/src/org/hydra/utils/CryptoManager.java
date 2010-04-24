package org.hydra.utils;

import org.jasypt.util.password.StrongPasswordEncryptor;

public final class CryptoManager {
	private static final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
	
	public static String encryptPassword(String inPassword){
		return passwordEncryptor.encryptPassword(inPassword);
	}
	
	public static boolean checkPassword(String inPassword, String inEncryptedPassword){
		return passwordEncryptor.checkPassword(inPassword, inEncryptedPassword);
	}
}
