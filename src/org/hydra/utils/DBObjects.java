package org.hydra.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

public final class DBObjects {
	static final Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");

	public static ERROR_CODES setObjects(
			String inAppId,
			List<Map<String, String>> objects, List<String> outKeys) {
		//if(mutator == null) { return(ERROR_CODES.ERROR_NO_VALID_MUTATOR); }
		if(objects == null || objects.size() == 0) { return(ERROR_CODES.ERROR_DB_EMPTY_VALUE); }
		for(Map<String, String> map:objects){
			// check & validation
			if(map.size() <= 1) { continue; }
			String objectName  = map.get(Constants._key_object_name);
			if(objectName == null || objectName.isEmpty()){ continue; }
			// remove objectName key & value due objectId generated
			map.remove(Constants._key_object_name);
			// update key id
			String objectId = DBUtils.getDBObjectID(objectName);
			map.put(objectName, objectId);
			for(Map.Entry<String, String> entry: map.entrySet()){
				// check for empty value
				if(entry.getValue() == null || entry.getValue().trim().isEmpty()) { continue; }
				// insert
				String name = (objectName == entry.getKey() ? objectName : objectName + '_' + entry.getKey());
				String value = entry.getValue();
				// mutator 
				ERROR_CODES err_code = DBUtils.setValue(inAppId, "Objects", objectId, name, value);
				if(err_code != ERROR_CODES.NO_ERROR) return(err_code);
//				Mutator<String> mutator = DBUtils.getMutator(inAppId);
//				mutator.addInsertion(objectId, "Objects", 
//						HFactory.createStringColumn((objectName == entry.getKey() ? objectName : objectName + '_' + entry.getKey()), entry.getValue()));
//				mutator.execute();
			}			
			if(outKeys != null) { outKeys.add(objectId); }
		}
		return(ERROR_CODES.NO_ERROR);
	}


	public static Map<String, Map<String, String>> getObjects(
			String inAppId,
			int rangeCount,
			String ... objectIds) {
		StringSerializer stringSerializer = StringSerializer.get();
		MultigetSliceQuery<String, String, String> query =
			    HFactory.createMultigetSliceQuery(
			    		DBUtils.getKspManager().getKeyspace(inAppId), 
			    		stringSerializer, stringSerializer, stringSerializer);
		query.setColumnFamily("Objects");
		query.setKeys(objectIds);
		query.setRange(null, null, false, rangeCount);
		QueryResult<Rows<String, String, String>> result = query.execute();
		Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
		for(Row<String, String, String> row:result.get()){
			map.put(row.getKey(), DBUtils.getMapFromRow(row));
		}
		return(map.size() == 0 ? null : map);
	}
	
	public static Map<String, Map<String, String>> getObjects(
			String inAppId,
			String ... columnNames) {
		StringSerializer stringSerializer = StringSerializer.get();
		MultigetSliceQuery<String, String, String> query =
			    HFactory.createMultigetSliceQuery(
			    		DBUtils.getKspManager().getKeyspace(inAppId), 
			    		stringSerializer, stringSerializer, stringSerializer);
		query.setColumnFamily("Objects");
		query.setKeys("");
		query.setColumnNames(columnNames);
		QueryResult<Rows<String, String, String>> result = query.execute();
		Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>();
		for(Row<String, String, String> row:result.get()){
			map.put(row.getKey(), DBUtils.getMapFromRow(row));
		}
		return(map.size() == 0 ? null : map);
	}
		
	

	public static ERROR_CODES deleteObjects(
			String inAppId,
			String ... objectIds) {
			return(DBUtils.deleteKeys(inAppId, "Objects", objectIds));
	}

}
