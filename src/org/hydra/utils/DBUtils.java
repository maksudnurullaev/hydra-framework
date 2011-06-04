package org.hydra.utils;

import java.util.List;

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
import org.hydra.deployers.Db;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class DBUtils {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");
	
	public static SimpleCassandraDao getSimpleCassandraDaoOrNull(
			String inKeyspace,
			String inColumnFamily){
		
		Result result = new Result();
		BeansUtils.getWebContextBean(result , Constants._bean_ksp_manager);
		if(result.isOk() && result.getObject() instanceof KspManager){
			KspManager kspManager = (KspManager) result.getObject();
			return(kspManager.getSimpleCassandraDao(inKeyspace, inColumnFamily));
		}
		_log.error(ErrorUtils.ERROR_CODES.ERROR_DB_NO_KSP.toString());
		return null;
	}
	
	public static ErrorUtils.ERROR_CODES getValue(
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
					if(inValue.getString() == null )
						return ErrorUtils.ERROR_CODES.ERROR_DB_NULL_VALUE;
					if(inValue.getString().isEmpty())
						return ErrorUtils.ERROR_CODES.ERROR_DB_EMPTY_VALUE;
					return ErrorUtils.ERROR_CODES.NO_ERROR;
				} catch (Exception e) {
					_log.error("... exception: " + e.getMessage());
					return ErrorUtils.ERROR_CODES.ERROR_DB_NO_DATABASE;
				}
				
			}else{
				return ErrorUtils.ERROR_CODES.ERROR_DB_NO_CF;
			}
		}
		return ErrorUtils.ERROR_CODES.ERROR_DB_NO_KSP;
	}

	public static ErrorUtils.ERROR_CODES setValue(
			String inKeyspace,
			String inColumnFamily, 
			String inKey,
			String inColumnName, 
			String value) {
		_log.debug("Try to insert...");
		_log.debug("inKeyspace: " + inKeyspace);
		_log.debug("inColumnFamily: " + inColumnFamily);
		_log.debug("inKey: " + inKey);
		_log.debug("inColumnName: " + inColumnName);
		_log.debug("value: " + value);
		try{
			Result result = new Result();
			BeansUtils.getWebContextBean(result , Constants._bean_ksp_manager);
			if(result.isOk() && result.getObject() instanceof KspManager){
				KspManager kspManager = (KspManager) result.getObject();
				SimpleCassandraDao s = kspManager.getSimpleCassandraDao(inKeyspace, inColumnFamily);
				if(s != null){
					s.insert(inKey, inColumnName, value);
					return ErrorUtils.ERROR_CODES.NO_ERROR;
				}else{
					return ErrorUtils.ERROR_CODES.ERROR_DB_NO_CF;
				}
			}
			return ErrorUtils.ERROR_CODES.ERROR_DB_NO_KSP;
		}catch(Exception e){
			_log.error(e.toString());
			return ErrorUtils.ERROR_CODES.ERROR_UKNOWN;
		}
	}
	
	public static int getCountOf(
			String inKeyspace,
			String inColumnFamily) {
		return getCountOf(inKeyspace, inColumnFamily, "", "", "", "");
	}
	
	public static int getCountOf(
			String inKeyspace,
			String inColumnFamily,
			String inKey) {
		return getCountOf(inKeyspace, inColumnFamily, inKey, inKey, "", "");
	}
	
	public static int getCountOf(
			String inKeyspace,
			String inColumnFamily,
			String startKey,
			String endKey,
			String startRange,
			String endRange){
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
	            rangeSlicesQuery.setKeys(startKey, endKey);
	            rangeSlicesQuery.setRange(startRange, endRange, false, 3);
	            
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
			String inKeyStart,
			String inKeyEnd,
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
	            rangeSlicesQuery.setKeys(inKeyStart, inKeyEnd);
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
	
	public static ErrorUtils.ERROR_CODES deleteKey(
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
		return ErrorUtils.ERROR_CODES.NO_ERROR;
	}
	
	return ErrorUtils.ERROR_CODES.ERROR_DB_NO_KSP;		
	}

	public static HColumn<String, String> getColumn(
			String inKeyspace,
			String inColumnFamily, 
			String inKey, 
			String inColumnName) {
		_log.debug("    inKeyspace: " + inKeyspace);
		_log.debug("inColumnFamily: " + inColumnFamily);
		_log.debug("         inKey: " + inKey);
		_log.debug("  inColumnName: " + inColumnName);
		try {		
			Result result = new Result();
			BeansUtils.getWebContextBean(result, Constants._bean_ksp_manager);
			if(result.isOk() && result.getObject() instanceof KspManager){
				_log.debug("KspManager exist: "	+ (result.isOk() && result.getObject() instanceof KspManager));
				KspManager kspManager = (KspManager) result.getObject();
				Keyspace keyspace = kspManager.getKeyspace(inKeyspace);
				if(keyspace != null){
					ColumnQuery<String, String, String> columnQuery = HFactory.createStringColumnQuery(keyspace);
					columnQuery.setColumnFamily(inColumnFamily).setKey(inKey).setName(inColumnName);
					QueryResult<HColumn<String, String>> result2 = columnQuery.execute();
					return(result2.get());
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

	public static String wrap2DivIfNeeds(
			String inKsp, 
			String inCFname,
			String inKey,
			String inCName,
			String inUserID, 		 // reserved
			Moder inModer,           // reserved
			List<String> links){
		Db._log.debug("Enter to: getDbTemplateKeyHow");
		// get result from DB
		StringWrapper content = new StringWrapper();
		ErrorUtils.ERROR_CODES err = getValue(inKsp, inCFname, inKey, inCName, content);
		switch (err) {
		case NO_ERROR:
			break;
		case ERROR_DB_EMPTY_VALUE:
			content.setString(String.format("<font color='red'>%s</font>",inKey));
		default:
			Db._log.warn(String.format("DB error with %s: %s", inKey, err.toString()));
			content.setString(String.format("<font color='red'>%s</font>",inKey, err.toString()));
		}
		if(Utils.hasRight2Edit(inKsp, inUserID, inModer))
			wrap2SpanEditObject(inKey, content, "DBRequest", inCFname, (err == ErrorUtils.ERROR_CODES.NO_ERROR), links);		
		return content.getString();			
	}

	public static void wrap2SpanEditObject(
			String inKey, 
			StringWrapper content, 
			String inHandlerName,
			String inEditObjectName,
			boolean noError, 
			List<String> links) {
	
		String spanId = String.format("%s.%s", inEditObjectName, inKey);
		String wrapString = String.format("<span class='edit' id='%s'>%s</span>", spanId, content.getString());
		content.setString(wrapString.toString());
		// List of Link
		if(links != null){
			StringBuffer result = new StringBuffer();
			
			// main link
			if(!noError)
				result.append("<a class='red' onclick=\"javascript:void(Globals.editIt('");
			else
				result.append("<a class='green' onclick=\"javascript:void(Globals.editIt('");
			result.append(inKey).append("','").append(inHandlerName).append("','").append("edit" + inEditObjectName)
						.append("')); return false;\" href=\"#\">").append(inKey).append("</a>");
			// sup - description
			result.append("<sup>(<a class='green' onclick=\"javascript:void(Globals.blinkIt('");
			result.append(spanId).append("')); return false;\" href=\"#\">").append(inEditObjectName).append("</a>)</sup>");
			
			links.add(result.toString());
		}
	}

	public static void testForNonExistenceKeyOrValue(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String inKeyspace, 
			String inColumnFamily,
			String inKey, 
			String inColumnName,
			String fieldID) {
		
		SimpleCassandraDao s = DBUtils.getSimpleCassandraDaoOrNull(inKeyspace, inColumnFamily);
		if(s == null){
			_log.error(ERROR_CODES.ERROR_DB_NO_CF);
			errorCodes.add(ERROR_CODES.ERROR_DB_NO_CF);
			errorFields.add(fieldID);
			return;
		}
		
		String value = s.get(inKey, inColumnName);
		if(value != null){
			_log.debug("testForNonExistenceKeyOrValue not passed!");
			errorCodes.add(ERROR_CODES.ERROR_DB_KEY_ALREADY_EXIST);
			errorFields.add(fieldID);			
		}		
	}	
}
