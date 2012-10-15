package org.hydra.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.KspManager;
import org.hydra.managers.CryptoManager;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class DBUtils {
	static final Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");
	private static KspManager _kspManager = null;
	
	public static SimpleCassandraDao getSimpleCassandraDaoOrNull(
			String inKeyspace,
			String inColumnFamily){
		
		KspManager kspManager = getKspManager();
		if(kspManager == null){ 
			_log.error("No keyspace");
			return(null); 
		}
		return(kspManager.getSimpleCassandraDao(inKeyspace, inColumnFamily));
	}
	
	public static ERROR_CODES getValue(
			String inAppId,
			String inColumnFamily, 
			String inKey,
			String inColumnName,
			StringWrapper inValue) {
		
		if(inKey == null || inKey.isEmpty()) { return(ERROR_CODES.ERROR_DB_NULL_VALUE); }

		SimpleCassandraDao s = getSimpleCassandraDaoOrNull(inAppId, inColumnFamily);
		if(s != null){
			_log.debug(String.format("Try to find key/column_name: %s/%s", inKey, inColumnName));
			try {
				inValue.setString(s.get(inKey, inColumnName));
				if(inValue.getString() == null )
					return(ERROR_CODES.ERROR_DB_NULL_VALUE);
				if(inValue.getString().isEmpty())
					return(ERROR_CODES.ERROR_DB_EMPTY_VALUE);
				return(ERROR_CODES.NO_ERROR);
			} catch (Exception e) {
				_log.error("... exception: " + e.getMessage());
				return(ERROR_CODES.ERROR_DB_NO_DATABASE);
			}	
		}
		return ERROR_CODES.ERROR_DB_NO_CF;
	}

	public static ERROR_CODES setValue(
			String inAppId,
			String inColumnFamily, 
			String inKey,
			String inColumnName, 
			String value) {
		
		SimpleCassandraDao s = getSimpleCassandraDaoOrNull(inAppId, inColumnFamily);

		if(s != null){
			s.insert(inKey, inColumnName, value);
			return ERROR_CODES.NO_ERROR;
		}
		return ERROR_CODES.ERROR_DB_NO_CF;
	}
	
	public static int getCountOf(
			String inAppId,
			String inColumnFamily) {
		return getCountOf(inAppId, inColumnFamily, "", "", "", "");
	}
	
	public static int getCountOf(
			String inAppId,
			String inColumnFamily,
			String inKey) {
		return getCountOf(inAppId, inColumnFamily, inKey, inKey, "", "");
	}
	
	public static int getCountOf(
			String inAppId,
			String inColumnFamily,
			String inKeyStart,
			String inKeyEnd,
			String inKeyRangeStart,
			String inKeyRangeFinish){

		List<Row<String, String, String>> rows = getRows(inAppId, inColumnFamily, inKeyStart, inKeyEnd, inKeyRangeStart, inKeyRangeFinish);
		if(rows != null){ return(rows.size()); }
		return(0);
	}	
	
	public static List<Row<String, String, String>> getRows(
			String inAppId,
			String inColumnFamily, 
			String inKeyStart,
			String inKeyEnd,
			String inKeyRangeStart,
			String inKeyRangeFinish) {
		List<Row<String, String, String>> resultRows = new ArrayList<Row<String,String,String>>();
		KspManager kspManager = getKspManager();
		if(kspManager == null){ 
			_log.error("No keyspace");
			return(null); 
		}
		StringSerializer stringSerializer = StringSerializer.get();
		
		Keyspace keyspace = kspManager.getKeyspace(inAppId);
		RangeSlicesQuery<String, String, String> rangeSlicesQuery = 
			HFactory.createRangeSlicesQuery(keyspace, stringSerializer, stringSerializer, stringSerializer);
				
        rangeSlicesQuery.setColumnFamily(inColumnFamily);
        rangeSlicesQuery.setKeys(inKeyStart, inKeyEnd);
        rangeSlicesQuery.setRange(inKeyRangeStart, inKeyRangeFinish, false, 3);
	            
        try {
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
        } catch (HectorException he) {
            _log.error(he);
        }		
        return(null);
	}		
	
	public static ERROR_CODES deleteKey(
			String inAppId, 
			String inColumnFamily,
			String inKey) {
		
		_log.debug(String.format("Try to delete Ksp/CFName/Key: %s/%s/%s", inAppId, inColumnFamily,inKey));
		
		KspManager kspManager = getKspManager();
		if(kspManager == null){ return(ERROR_CODES.ERROR_DB_NO_KSP); }

		Mutator<String> mutator = getMutator(inAppId);
		if(mutator == null) { return(ERROR_CODES.ERROR_NO_VALID_MUTATOR); }
		try {
			mutator.delete(inKey, inColumnFamily, null, StringSerializer.get());
			mutator.execute();
		} catch (Exception e) {
			_log.error(e.toString());
			return(ERROR_CODES.ERROR_UKNOWN);
		}
		return ERROR_CODES.NO_ERROR;
	}
	
	public static Mutator<String> getMutator(String inAppId){
		KspManager kspManager = getKspManager();
		if(kspManager == null){
			_log.error("Could not find KspManager for: " + inAppId);
			return(null);
		}
		StringSerializer stringSerializer = StringSerializer.get();			
		return(HFactory.createMutator(kspManager.getKeyspace(inAppId), stringSerializer));
	}
	
	public static KspManager getKspManager(){
		if(_kspManager != null){ return(_kspManager); }
		Result result = new Result();
		BeansUtils.getWebContextBean(result , Constants._bean_ksp_manager);
		if(result.isOk() && result.getObject() instanceof KspManager){
			_kspManager = (KspManager) result.getObject();
		}
		return(_kspManager);
	}

	public static HColumn<String, String> getColumn(
			String inAppId,
			String inColumnFamily, 
			String inKey, 
			String inColumnName) {

		KspManager kspManager = getKspManager();
		if(kspManager == null){ 
			_log.error("No keyspace");
			return(null); 
		}		
		
		Keyspace keyspace = kspManager.getKeyspace(inAppId);
		if(keyspace == null) {
			_log.error("keyspace: " + inKey + " == null)");
			return null;
		}
		ColumnQuery<String, String, String> columnQuery = HFactory.createStringColumnQuery(keyspace);
		columnQuery.setColumnFamily(inColumnFamily).setKey(inKey).setName(inColumnName);
		try {		
			QueryResult<HColumn<String, String>> result = columnQuery.execute();
			return(result.get());
        } catch (HectorException he) {
            _log.error(he.toString());
            return null;
        } 
	}

	public static QueryResult<ColumnSlice<String, String>> getColumns(
			String inAppId,
			String inColumnFamily, 
			String inKey) {

		KspManager kspManager = getKspManager();
		if(kspManager == null){ 
			_log.error("No keyspace");
			return(null); 
		}		
		
		Keyspace keyspace = kspManager.getKeyspace(inAppId);
		if(keyspace == null) {
			_log.error("keyspace: " + inKey + " == null)");
			return null;
		}
		StringSerializer stringSerializer = StringSerializer.get();
		SliceQuery<String, String, String> sq = HFactory.createSliceQuery(keyspace, stringSerializer, stringSerializer, stringSerializer);
		sq.setColumnFamily(inColumnFamily);
		sq.setKey(inKey);
		sq.setRange("", "", false, 3);
		try {		
			QueryResult<ColumnSlice<String, String>> result = sq.execute();
			return(result);
        } catch (HectorException he) {
            _log.error(he.toString());
            return null;
        } 
	}	
	
	public static void testForNonExistenceOfKey(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String inAppId, 
			String inColumnFamily,
			String inKey, 
			String fieldID) {
		
		int foundRows = getRows(inAppId, inColumnFamily, inKey, inKey, "", "").size();
		if(foundRows != 0){
			_log.warn(Utils.F("%s.%s['%s'] already exist!", 
					inAppId, inColumnFamily,inKey));
			errorCodes.add(ERROR_CODES.ERROR_DB_KEY_ALREADY_EXIST);
			errorFields.add(fieldID);	
		}
		
	}	

	public static String testForExistenceOfKeyAndValue(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String inAppId, 
			String inColumnFamily,
			String inKey, 
			String inColumnName,
			String fieldID) {
		
		SimpleCassandraDao s = DBUtils.getSimpleCassandraDaoOrNull(inAppId, inColumnFamily);
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
			return(null);
		}	
		return(value);
	}

	public static Map<String, String> testForExistenceKey(
			List<String> errorFields,
			List<ERROR_CODES> errorCodes, 
			String inAppId, 
			String inColumnFamily,
			String inKey,
			String fieldID) {
		Map<String, String> result = new HashMap<String, String>();
		List<Row<String, String, String>> rows = getRows(inAppId, inColumnFamily, inKey, inKey, "", "");
		if(rows.size() == 1	&& rows.get(0).getColumnSlice() != null ){
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
	
	public static boolean test4GlobalAdmin(String inKey, String inPassword) {
		Result result = new Result();
		try {		
			BeansUtils.getWebContextBean(result, Constants._bean_ksp_manager);
			if(result.isOk() && result.getObject() instanceof KspManager){
				KspManager kspManager = (KspManager) result.getObject();
				if(kspManager.getAdministrators().size() > 0){
					return(	kspManager.getAdministrators().containsKey(inKey) 
							&& CryptoManager.checkPassword( inPassword, kspManager.getAdministrators().get(inKey) ) );
				}
			}
        } catch (Exception e) {
            _log.error(e.toString());
            return false;
        }		
        return false; 
	}

	public static ERROR_CODES setObjects(
			String inAppId,
			List<Map<String, String>> objects, List<String> outKeys) {
		Mutator<String> mutator = getMutator(inAppId);
		if(mutator == null) { return(ERROR_CODES.ERROR_NO_VALID_MUTATOR); }
		if(objects == null || objects.size() == 0) { return(ERROR_CODES.ERROR_DB_EMPTY_VALUE); }
		for(Map<String, String> map:objects){
			// check & validation
			if(map.size() <= 1) { continue; }
			String objectName  = map.get(Constants._key_object_name);
			if(objectName == null || objectName.isEmpty()){ continue; }
			// remove objectName key & value due objectId generated
			map.remove(Constants._key_object_name);
			// update key id
			String objectId = GetDBObjectID(objectName);
			for(Map.Entry<String, String> entry: map.entrySet()){
				mutator.addInsertion(objectId, "Objects", HFactory.createStringColumn(entry.getKey(), entry.getValue()));
			}
			mutator.execute();
			if(outKeys != null) { outKeys.add(objectId); }
		}
		return(ERROR_CODES.NO_ERROR);
	}


	public static String GetDBObjectID(String inObjectName){
		return ( inObjectName + " " + Utils.GetCurrentDateTime() + " " + Utils.GetUUID().substring(0,2));		
	}	

	public static Map<String, String> getColumnsAsMap(
			String inAppId,
			String inColumnFamily, 
			String inKey) {
		QueryResult<ColumnSlice<String, String>> result = getColumns(inAppId, inColumnFamily, inKey);
		if(result == null || result.get() == null || result.get().getColumns().size() == 0) { return(null);}
		Map<String, String> map = new HashMap<String, String>();
		for(HColumn<String, String> c:result.get().getColumns()){ map.put(c.getName(), c.getValue()); }
		return(map);
	}

	public static Map<String, Map<String, String>> getObjectsAsMap(
			String inAppId, List<String> objectIds) {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
		for(String id: objectIds){ result.put(id, getColumnsAsMap(inAppId, "Objects", id)); }
		return(result);
	}
}

