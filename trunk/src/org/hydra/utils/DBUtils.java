package org.hydra.utils;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

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
	
	public static int getCountOf(
			String inKeyspace,
			String inColumnFamily) {
		try {		
			Result result = new Result();
			StringSerializer stringSerializer = StringSerializer.get();
			BeansUtils.getWebContextBean(result, Constants._bean_ksp_manager);
			if(result.isOk() && result.getObject() instanceof KspManager){
				KspManager kspManager = (KspManager) result.getObject();
				Keyspace keyspace = kspManager.getKeyspace(inKeyspace);
				RangeSlicesQuery<String, String, String> rangeSlicesQuery = 
					HFactory.createRangeSlicesQuery(keyspace, stringSerializer, stringSerializer, stringSerializer);
				
	            rangeSlicesQuery.setColumnFamily(inColumnFamily);
	            rangeSlicesQuery.setKeys("", "");
	            rangeSlicesQuery.setRange("", "", false, 3);
	            
	            QueryResult<OrderedRows<String, String, String>> resultOfExec = rangeSlicesQuery.execute();
	            OrderedRows<String, String, String> orderedRows = resultOfExec.get();
	            return orderedRows.getCount();
			}
        } catch (HectorException he) {
            he.printStackTrace();
        }		
		_log.error(String.format("Could not get count of: %s(%s)", inColumnFamily, inKeyspace));
		return -1;
	}

	public static OrderedRows<String,String,String> getRows(
			String inKeyspace,
			String inColumnFamily, 
			String inKeyRangeStart,
			String inKeyRangeFinish) {
		try {		
			Result result = new Result();
			StringSerializer stringSerializer = StringSerializer.get();
			BeansUtils.getWebContextBean(result, Constants._bean_ksp_manager);
			if(result.isOk() && result.getObject() instanceof KspManager){
				KspManager kspManager = (KspManager) result.getObject();
				Keyspace keyspace = kspManager.getKeyspace(inKeyspace);
				RangeSlicesQuery<String, String, String> rangeSlicesQuery = 
					HFactory.createRangeSlicesQuery(keyspace, stringSerializer, stringSerializer, stringSerializer);
				
	            rangeSlicesQuery.setColumnFamily(inColumnFamily);
	            rangeSlicesQuery.setKeys("", "");
	            rangeSlicesQuery.setRange(inKeyRangeStart, inKeyRangeFinish, false, 3);
	            
	            QueryResult<OrderedRows<String, String, String>> resultOfExec = rangeSlicesQuery.execute();
	            return(resultOfExec.get());
			}
        } catch (HectorException he) {
            _log.error(he);
            return null;
        }		
        return null; 
	}

	
	public static ERROR_CODES deleteKey(
			String appId, 
			String inColumnFamily,
			String inKey) {
		
	_log.debug(String.format("Try to delete Ksp/CFName/Key: %s/%s/%s", appId, inColumnFamily,inKey));
	
	Result result = new Result();
	BeansUtils.getWebContextBean(result , Constants._bean_ksp_manager);
	if(result.isOk() && result.getObject() instanceof KspManager){
		KspManager kspManager = (KspManager) result.getObject();
		StringSerializer stringSerializer = StringSerializer.get();			
		Mutator<String> mutator = HFactory.createMutator(kspManager.getKeyspace(appId), stringSerializer);
		mutator.delete(inKey, inColumnFamily, null, stringSerializer);
		mutator.execute();
		return ERROR_CODES.NO_ERROR;
	}
	
	return ERROR_CODES.ERROR_NO_KSP;		
	}

	public static HColumn<String, String> getColumn(
			String inKeyspace,
			String inColumnFamily, 
			String inKey, 
			String inColumnName) {
		_log.error("    inKeyspace: " + inKeyspace);
		_log.error("inColumnFamily: " + inColumnFamily);
		_log.error("         inKey: " + inKey);
		_log.error("  inColumnName: " + inColumnName);
		try {		
			Result result = new Result();
			BeansUtils.getWebContextBean(result, Constants._bean_ksp_manager);
			_log.error("Result of (result.isOk() && result.getObject() instanceof KspManager): " 
					+ (result.isOk() && result.getObject() instanceof KspManager));
			if(result.isOk() && result.getObject() instanceof KspManager){
				KspManager kspManager = (KspManager) result.getObject();
				Keyspace keyspace = kspManager.getKeyspace(inKeyspace);
				if(keyspace != null){
					ColumnQuery<String, String, String> columnQuery = HFactory.createStringColumnQuery(keyspace);
					columnQuery.setColumnFamily(inColumnFamily).setKey(inKey).setName(inColumnName);
					_log.error("Result of (columnQuery == null): " + (columnQuery == null)); 
					QueryResult<HColumn<String, String>> result2 = columnQuery.execute();
					_log.error("Result of (result2 == null): " + (result2 == null));
					_log.error("Result of (result2.get() == null): " + (result2.get() == null));
					return(result2.get());
//					return(result2);
//					return null;
				}else{
					_log.error("keyspace: " + inKey + " == null)");
					return null;
				}
			}else{
				_log.error("!(result.isOk() && result.getObject() instanceof KspManager)");
				return null;
			}			
        } catch (HectorException he) {
            _log.error(he);
            return null;
        } 
	}	
}
