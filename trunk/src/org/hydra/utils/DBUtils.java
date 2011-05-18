package org.hydra.utils;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.KspManager;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class DBUtils {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");
	
	public static enum ERROR_CODES{
		NO_ERROR,
		ERROR_NO_VALUE,
		ERROR_NO_DATABASE, 
		ERROR_NO_CF, 
		ERROR_NO_KSP
	}; 

	public static ERROR_CODES getValue(
			String inKeyspace,
			String inColumnFamily, 
			String inKey,
			String inColumnName,
			StringWrapper inValue) {
		
		Result result = new Result();
		BeansUtils.getWebContextBean(result , Constants._bean_ksp_manager);
		if(result.isOk() && result.getObject() instanceof KspManager){
			KspManager kspManager = (KspManager) result.getObject();
			SimpleCassandraDao s = kspManager.getSimpleCassandraDao(inKeyspace, inColumnFamily);
			if(s != null){
				_log.debug(String.format("Try to find key/column_name: %s/%s", inKey, inColumnName));
				try {
					inValue.setString(s.get(inKey, inColumnName));
					if(inValue.getString() == null){
						_log.warn(inKey + " == null");
						return ERROR_CODES.ERROR_NO_VALUE;
					}
					return ERROR_CODES.NO_ERROR;
				} catch (Exception e) {
					_log.error("... exception: " + e.getMessage());
					return ERROR_CODES.ERROR_NO_DATABASE;
				}
				
			}else{
				return ERROR_CODES.ERROR_NO_CF;
			}
		}
		return ERROR_CODES.ERROR_NO_KSP;
	}

	public static ERROR_CODES setValue(
			String inKeyspace,
			String inColumnFamily, 
			String inKey,
			String inColumnName, 
			String value) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result , Constants._bean_ksp_manager);
		if(result.isOk() && result.getObject() instanceof KspManager){
			KspManager kspManager = (KspManager) result.getObject();
			SimpleCassandraDao s = kspManager.getSimpleCassandraDao(inKeyspace, inColumnFamily);
			if(s != null){
				_log.debug(String.format("Try to find key/column_name: %s/%s", inKey, inColumnName));
				s.insert(inKey, inColumnName, value);
				return ERROR_CODES.NO_ERROR;
			}else{
				return ERROR_CODES.ERROR_NO_CF;
			}
		}
		return ERROR_CODES.ERROR_NO_KSP;
		
	}
}
