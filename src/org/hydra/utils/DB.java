package org.hydra.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.h2.jdbcx.JdbcConnectionPool;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DB {
	private static Log _log = LogFactory.getLog("org.hydra.utils.CaptchaUtils");
	static JdbcConnectionPool cp =JdbcConnectionPool.create("jdbc:h2:file:db/buh1", "sa", "sa");
	
	public static Connection getConnection(){
		try {
			return(cp.getConnection());
		} catch (SQLException e) {
			_log.error(e.toString());
		}
		return(null);
	}

	public static ERROR_CODES getValue(
			String inAppId, 
			String inObjectName,
			String inId, 
			String inFieldName, 
			StringWrapper sWrapper) {
		_log.error("inAppId: " + inAppId);
		_log.error("inObjectName: " +inObjectName);
		_log.error("inId: " + inId);
		_log.error("inFieldName: " + inFieldName);
		throw new NotImplementedException();
	}

	public static List<Object> getList(
			String inAppID, 
			String inObjectName) {
		throw new NotImplementedException();
	}

	public static Map<String, String> isExist(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes,
			String inAppID, 
			String inObjectName,
			String inId) {
		throw new NotImplementedException();
	}

}
