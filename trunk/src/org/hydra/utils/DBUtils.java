package org.hydra.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
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
import org.hydra.managers.CryptoManager;
import org.hydra.messages.interfaces.IMessage;
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
			String inKeyStart,
			String inKeyEnd,
			String inKeyRangeStart,
			String inKeyRangeFinish){

		List<Row<String, String, String>> rows = 
			getValidRows(inKeyspace, inColumnFamily, inKeyStart, inKeyEnd, inKeyRangeStart, inKeyRangeFinish);
		if(rows != null)
			return(rows.size());
		return(0);
	}	
	
	public static List<Row<String, String, String>> getValidRows(
			String inKeyspace,
			String inColumnFamily, 
			String inKeyStart,
			String inKeyEnd,
			String inKeyRangeStart,
			String inKeyRangeFinish) {
		List<Row<String, String, String>> resultRows = new ArrayList<Row<String,String,String>>();
		Result result = new Result();
		StringSerializer stringSerializer = StringSerializer.get();
		try {		
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
	            OrderedRows<String, String, String> ol = resultOfExec.get();
	            for(Row<String, String, String> row: ol){
	            	if(row.getColumnSlice() != null 
	            			&& row.getColumnSlice().getColumns() != null
	            			&& row.getColumnSlice().getColumns().size() > 0){
	            		resultRows.add(row);
	            	}
	            }
	            return(resultRows);
			}
        } catch (HectorException he) {
            _log.error(he);
            resultRows.clear();
            return resultRows;
        }		
        resultRows.clear();
        return resultRows; 
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

	public static String wrap2IfNeeds(
			String inKsp, 
			String inCFname,
			String inKey,
			String inCName,
			IMessage inMessage,
			Map<String, String> editLinks,
			String inWrapper){
		Db._log.debug("Enter to: getDbTemplateKeyHow");
		// test user access by key
		int keyLevel = Utils.isSpecialKey(inKey); 
		if(keyLevel >= 0){
			if(!Roles.roleNotLessThen(keyLevel, inMessage))
				return "";
		}
		// get result from DB
		StringWrapper content = new StringWrapper();
		ErrorUtils.ERROR_CODES err = getValue(inKsp, inCFname, inKey, inCName, content);
		switch (err) {
		case NO_ERROR:
			break;
		case ERROR_DB_EMPTY_VALUE:
		case ERROR_DB_NULL_VALUE:
			content.setString(String.format("<font color='red'>%s</font>",inKey));
			break;
		default:
			_log.error(String.format("DB error with %s: %s", inKey, err.toString()));
			content.setString(String.format("<font color='red'>%s</font>",inKey, err.toString()));
		}
		if(Roles.roleNotLessThen(Roles.USER_EDITOR, inMessage))
			wrap2EditObject(inKey, content, "DBRequest", inCFname, Utils.errDBCodeValueExest(err), editLinks, inWrapper);
		
		return content.getString();			
	}

	public static void wrap2EditObject(
			String inKey, 
			StringWrapper content, 
			String inHandlerName,
			String inEditObjectName,
			boolean noError, 
			Map<String, String> editLinks,
			String inWrapper) {
	
		String spanId = String.format("%s.%s", inEditObjectName, inKey);
		String wrapString = String.format("<%s id='%s'>%s</%s>", inWrapper, spanId, content.getString(), inWrapper);
		content.setString(wrapString.toString());
		// List of Link
		if(editLinks != null && !editLinks.containsKey(spanId)){
			StringBuffer result = new StringBuffer();
			
			// main link
			if(!noError)
				result.append("<a class='red' onclick=\"javascript:void(Globals.editIt('");
			else
				result.append("<a class='green' onclick=\"javascript:void(Globals.editIt('");
			result.append(inKey).append("','").append(inHandlerName).append("','").append("edit" + inEditObjectName)
						.append("')); return false;\" href=\"#\">").append(inKey).append("</a>");
			// sup - description
			result.append("<sup>(<a class='green' onclick=\"javascript:void(Globals.toggle('");
			result.append(spanId).append("')); return false;\" href=\"#\">").append(inEditObjectName).append("</a>, ")
				.append(wrapString.toString().length()).append(")</sup>");
			
			editLinks.put(spanId, result.toString());
		}
	}

	public static void testForNonExistenceOfKey(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String inKeyspace, 
			String inColumnFamily,
			String inKey, 
			String fieldID) {
		
		int foundRows = getValidRows(inKeyspace, inColumnFamily, inKey, inKey, "", "").size();
		if(foundRows != 0){
			_log.warn(Utils.F("%s.%s['%s'] already exist!", 
					inKeyspace, inColumnFamily,inKey));
			errorCodes.add(ERROR_CODES.ERROR_DB_KEY_ALREADY_EXIST);
			errorFields.add(fieldID);	
		}
		
	}	

	public static String testForExistenceOfKeyAndValue(
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
			return null;
		}
		
		String value = s.get(inKey, inColumnName);
		if(value == null || value.isEmpty()){
			_log.debug("testForExistenceOfKeyAndValue not passed!");
			errorCodes.add(ERROR_CODES.ERROR_DB_KEY_OR_VALUE_NOT_EXIST);
			errorFields.add(fieldID);			
		}	
		return(value);
	}

	public static Map<String, String> testForExistenceKey(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String inKeyspace, 
			String inColumnFamily,
			String inKey,
			String fieldID) {
		Map<String, String> result = new HashMap<String, String>();
		List<Row<String, String, String>> rows = getValidRows(inKeyspace, inColumnFamily, inKey, inKey, "", "");
		if(rows.size() == 1 
				&& rows.get(0).getColumnSlice() != null
				){
			for(HColumn<String, String> hc: rows.get(0).getColumnSlice().getColumns()){
				result.put(hc.getName(), hc.getValue());
			}
		}else{
			_log.debug("testForExistenceKey not passed!");
			errorCodes.add(ERROR_CODES.ERROR_DB_KEY_OR_VALUE_NOT_EXIST);
			errorFields.add(fieldID);
		}
		return(result);
	}	
	
	public static boolean test4GlobalAdmin(String key, String password) {
		Result result = new Result();
		try {		
			BeansUtils.getWebContextBean(result, Constants._bean_ksp_manager);
			if(result.isOk() && result.getObject() instanceof KspManager){
				KspManager kspManager = (KspManager) result.getObject();
				if(kspManager.getAdministrators().size() > 0){
					if(kspManager.getAdministrators().containsKey(key) &&
							CryptoManager.checkPassword(password, kspManager.getAdministrators().get(key))){
						return true;
					}
				}
			}
        } catch (Exception e) {
            _log.error(e.toString());
            return false;
        }		
        return false; 
	}	
}

