package org.hydra.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbcx.JdbcConnectionPool;
import org.hydra.beans.abstracts.APropertyLoader;

public final class DBUtils {
	public static enum QUERY_TYPE{
		INSERT,
		UPDATE,
		DELETE,
		SELECT,
		SELECT_COUNT,
	};
	private static Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");
	public static Properties prop = new Properties();    	 	
	static JdbcConnectionPool cp = null;

	public static void init_cp(){
		prop = APropertyLoader.loadProperties("buh1");
        cp = JdbcConnectionPool.create(
       		prop.getProperty("db.connection.string"), 
       		prop.getProperty("db.user.name"), 
       		prop.getProperty("db.user.password"));
	}
	
	public void finalize(){
		if(cp != null) cp.dispose();
	}
	
	public static Connection getConnection(){
		if(cp == null){ init_cp(); }
		try {
			return(cp.getConnection());
		} catch (SQLException e) {
			_log.error(e.toString());
		}
		return(null);
	}

	public static boolean validateData(QUERY_TYPE queryType, Map<String, String> data) {
		if(data == null) { return(false); }
		switch (queryType) {
		case INSERT:
		{
			if((data.containsKey("_object"))
					&& (!data.containsKey("_key"))) {
				String objectName = data.get("_object");
				if(objectName == null || objectName.trim().isEmpty()) { return(false); }
				data.put("_key", DBUtils.getDBId(data.get("_object").trim().toLowerCase()));
				data.remove("_object");
			}else if(data.containsKey("_key")) {
				String objectKey = data.get("_key");
				if(objectKey == null || objectKey.trim().isEmpty()) { return(false); }
				data.remove("_object");
			}
			return(data.size() > 1);
		}
		case UPDATE:
		{
			if(!data.containsKey("_key")) { return(false); }
			String objectKey = data.get("_key");
			if(objectKey == null || objectKey.trim().isEmpty()) { return(false); }
			data.remove("_object");
			return(data.size() > 1);
		}
		case DELETE:
		{
			if(!data.containsKey("_key")) { return(false); }
			String objectKey = data.get("_key");
			if(objectKey == null || objectKey.trim().isEmpty()) { return(false); }			
			return(true);
		}
		case SELECT:
			if(data.containsKey("_key")) { 
				String objectKey = data.get("_key");
				if(objectKey == null || objectKey.trim().isEmpty()) { return(false); }
				return(true);
			} else if(data.containsKey("_object")) { 
				String objectName = data.get("_object");
				if(objectName == null || objectName.trim().isEmpty()) { return(false); }
				return(true);
			}
		case SELECT_COUNT:
			return(data.containsKey("_object"));
		}
		return(false);
	}

	private static String getDBId(String objectName) {
		return(String.format("%s %s %s", objectName, Utils.GetDateUUID(), Utils.GetUUID().substring(0, 2)));
	}

	public static String insert_format = " INSERT INTO OBJECTS (KEY, FIELD_NAME, FIELD_VALUE, FIELD_ORDER) VALUES('%s', '%s', '%s', %s)  ; ";
	public static String update_format = " UPDATE OBJECTS SET FIELD_VALUE = '%s' WHERE KEY = '%s' AND FIELD_NAME = '%s' ; ";
	public static String select_format = " SELECT KEY, FIELD_NAME, FIELD_VALUE, FIELD_ORDER  FROM OBJECTS %s ORDER BY KEY DESC, FIELD_ORDER ; ";
	public static String delete_format = " DELETE FROM OBJECTS WHERE %s ;";
	public static String select2find_field_name_existance = "SELECT FIELD_NAME FROM OBJECTS WHERE KEY = '%s' AND FIELD_NAME = '%s' ; " ;
	public static String select4count  = " SELECT COUNT(DISTINCT KEY) FROM OBJECTS %s ;  "; 

	public static LinkedList<String> makeQueries(
			QUERY_TYPE queryType,
			Map<String, String> data) {
		LinkedList<String> result = new LinkedList<String>();
			switch (queryType) {
			case INSERT:
				for(Map.Entry<String, String> entry: data.entrySet()){
					if(entry.getKey().startsWith("_")){ continue; } // don't use special fields
					makeQueriesWithCheckingFiledSize(
							QUERY_TYPE.INSERT, 
							result, data.get("_key"),
							entry.getKey(),
							entry.getValue());
				}
				break;
			case UPDATE:
				for(Map.Entry<String, String> entry: data.entrySet()){
					if(entry.getKey().startsWith("_")){ continue; } // don't use special fields
					makeQueriesWithCheckingFiledSize(
							QUERY_TYPE.INSERT, 
							result, data.get("_key"),
							entry.getKey(),
							entry.getValue());

				}
				break;
			case SELECT:
				result.add(String.format(select_format,	getWherePart(data)));
				break;
			case SELECT_COUNT:
				result.add(String.format(select4count, getWherePart(data)));
				break;
			case DELETE:
				result.add(String.format(delete_format,	String.format(" KEY = '%s' ", data.get("_key"))));
			default:
				break;
			}
		return (result);
	}

	private static void makeQueriesWithCheckingFiledSize(
			QUERY_TYPE insert,
			LinkedList<String> quieriesList, 
			String key, 
			String fieldName, 
			String fieldValue) {
		if(insert == QUERY_TYPE.UPDATE){
			quieriesList.add(String.format(delete_format, String.format(" KEY = '%s' AND FIELD_NAME = '%s' ", key, fieldName)));
		}		
		int valueLength = fieldValue.length();
		if( valueLength > 255 ){
			int endRange =  0;
			int order = 0;
			for(int i=0 ; i<valueLength; i+=255){
				endRange = i+255;
				if(endRange >= valueLength) endRange = valueLength;
				quieriesList.add(String.format(insert_format,
						key,
						fieldName,
						fieldValue.substring(i, endRange),
						++order
						));
			}		
		} else {
			quieriesList.add(String.format(insert_format,
					key,
					fieldName,
					fieldValue,
					0
					));			
		}
	}

	private static String getWherePart(Map<String, String> data) {
		String wherePart = "";
		if(data.containsKey("_key")){
			String key = data.get("_key");
			wherePart = " WHERE KEY = '" + key + "' ";
		} else if(data.containsKey("_object")){
			String objectName = data.get("_object");
			wherePart = " WHERE KEY LIKE '" + objectName.toLowerCase() + "%'" ; 
		}
		// where #2
		String wherePart2 = "";
		for(Map.Entry<String, String> entry: data.entrySet()){
			if(entry.getKey().startsWith("_")){ continue; } // don't use special fields 
			if(entry.getValue() != null && (!entry.getValue().isEmpty())){
				if(wherePart2.isEmpty()){
					wherePart2 = " (FIELD_NAME = '" + entry.getKey() + "' AND FIELD_VALUE " + entry.getValue() + ") ";
				} else {
					wherePart2 += " OR (FIELD_NAME = '" + entry.getKey() + "' AND FIELD_VALUE " + entry.getValue() + ") ";
				}
			}
		}
		if(wherePart.isEmpty()){
			wherePart = wherePart2.isEmpty() ? "" : " WHERE " + wherePart2 + " ";
		} else {
			wherePart +=  wherePart2.isEmpty() ? "" : " AND (" + wherePart2 + ") ";					
		}
		return wherePart;
	}

	public static Map<String, Map<String, String>> sortMapByFoundFields(
			Map<String, Map<String, String>> map, int expectedFiledCount) {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
		for(Map.Entry<String, Map<String, String>> entry: map.entrySet()){
			if(entry.getValue().size() >= expectedFiledCount){
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return(result);
	}

	public static Map<String, List<String>> sortMapByPages(
			List<String> keys, 
			int pageSize) {
		Map<String, List<String>> pages = new HashMap<String, List<String>>();
		int rowsCount = keys.size();
		if(rowsCount <= pageSize){
			pages.put("1", keys);
		} else {
			int module = rowsCount % pageSize;
			int pagesCount = (rowsCount - module) / pageSize;
			if(module != 0) { pagesCount += 1; }
			for(int i = 1; i<=pagesCount; i++){
				pages.put(""+i, DBUtils.getKeysListPageFromMap(keys, i, pageSize));
			}
		}
		return(pages);
	}

	private static List<String> getKeysListPageFromMap(
			List<String> keys, 
			int pageNumber,
			int pageSize) {
		int keysSize = keys.size();
		int startElement = (pageNumber - 1) * pageSize;
		int endElement = startElement + pageSize;
		String [] keysAsArray = keys.toArray(new String[keysSize]);
		List<String> result = new ArrayList<String>(); 
		for(int i=(startElement); i<endElement; i++){
			if(i<keysSize){
				result.add(keysAsArray[i]);
			} else { break; }
		}
		return(result);
	}

	public static List<String> getKeysAsList(
			Map<String, Map<String, String>> map) {
		return(new ArrayList<String>(map.keySet()));
	}

}
