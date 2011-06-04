package org.hydra.utils;

public final class ErrorUtils {

	public static enum ERROR_CODES{
// COMMON		
		ERROR_UKNOWN,
		NO_ERROR,
// DB 		
		ERROR_DB_NULL_VALUE,
		ERROR_DB_EMPTY_VALUE,
		ERROR_DB_NO_DATABASE, 
		ERROR_DB_NO_CF, 
		ERROR_DB_NO_KSP,
		ERROR_DB_KEY_ALREADY_EXIST,
		ERROR_DB_DATA_TOO_MANY,
// FROM FIELDS
		ERROR_NO_VALID_MAIL, 
		ERROR_NO_VALID_PASSWORDS
	}

}
