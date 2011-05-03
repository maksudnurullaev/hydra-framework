package org.hydra.utils;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		ERROR_NO_CF_BEAN
	}; 

	public static ERROR_CODES getValue(
			String inKeyspace,
			String inColumnFamily, 
			String inKey,
			String inСolumnName,
			StringWrapper inValue) {
		
		
		String appIdCfBean = "cf" + inKeyspace + inColumnFamily;
		Result result = new Result();
		BeansUtils.getWebContextBean(result , appIdCfBean);
		
		if(result.isOk() && result.getObject() instanceof SimpleCassandraDao){
			_log.debug("Bean " + appIdCfBean + " found!");
			SimpleCassandraDao s = (SimpleCassandraDao) result.getObject();
			_log.debug(String.format("Try to find key/column_name: %s/%s", inKey, inСolumnName));
			try {
				inValue.setString(s.get(inKey, inСolumnName));
				if(inValue.getString() == null){
					_log.warn("value == null");
					return ERROR_CODES.ERROR_NO_VALUE;
				}
				return ERROR_CODES.NO_ERROR;
			} catch (Exception e) {
				_log.error("... exception: " + e.getMessage());
				return ERROR_CODES.ERROR_NO_DATABASE;
			}
		}
		_log.error("Bean is not istance of SimpleCassandraDao!");
		return ERROR_CODES.ERROR_NO_CF_BEAN;
	}
}
