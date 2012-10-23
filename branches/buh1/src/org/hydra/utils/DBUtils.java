package org.hydra.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbcx.JdbcConnectionPool;

public final class DBUtils {
	public static enum QUERY_TYPE{
		INSERT,
		UPDATE,
		DELETE,
		SELECT
	};
	private static Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");
	private static boolean _db_objects_created = false;
	static JdbcConnectionPool cp =JdbcConnectionPool.create("jdbc:h2:file:db/buh1", "sa", "sa");

	public static LinkedList<String> getDBObjectsCreateStatements() {
		LinkedList<String> stmts = new LinkedList<String>();
		stmts.add("CREATE TABLE Objects (ID INT PRIMARY KEY AUTO_INCREMENT, KEY VARCHAR(64), FIELD_NAME VARCHAR(64), FIELD_ORDER INT DEFAULT 0 NOT NULL, FIELD_VALUE VARCHAR(255));");
		stmts.add("CREATE INDEX iKEY ON Objects(KEY);");
		stmts.add("CREATE INDEX iFIELD_NAME ON Objects(FIELD_NAME);");
		stmts.add("CREATE INDEX iFIELD_ORDER ON Objects(FIELD_ORDER);");
		return(stmts);
	}

	public static Connection getConnection(){
		if(!_db_objects_created){
			createDBObjectsIfNotExist();
		}
		try {
			return(cp.getConnection());
		} catch (SQLException e) {
			_log.error(e.toString());
		}
		return(null);
	}

	private static void createDBObjectsIfNotExist() {
		LinkedList<String> stmts = DBUtils.getDBObjectsCreateStatements();
		try {
			Connection c = cp.getConnection();
			ResultSet rs =  c.getMetaData().getTables(c.getCatalog(), null, "OBJECTS", null);
			if(!rs.next()){ // table Objects not found!!!
				while(!stmts.isEmpty()){
					c.prepareStatement(stmts.remove()).execute();
				}
				c.commit();
				c.close();
			}
			// change to prevent do it each time
			_db_objects_created = true;
		} catch (SQLException e) {
			_log.error(e.toString());
		}
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
		}
		return(false);
	}

	private static String getDBId(String objectName) {
		return(String.format("%s %s %s", objectName, Utils.GetDateUUID(), Utils.GetUUID().substring(0, 2)));
	}

	public static String insert_format = " INSERT INTO OBJECTS (KEY, FIELD_NAME, FIELD_VALUE) VALUES('%s', '%s', '%s')  ; ";
	public static String update_format = " UPDATE OBJECTS SET FIELD_VALUE = '%s' WHERE KEY = '%s' AND FIELD_NAME = '%s' ; ";
	public static String select_format = " SELECT KEY, FIELD_NAME, FIELD_VALUE, FIELD_ORDER  FROM OBJECTS %s ; ";
	public static String delete_format = " DELETE FROM OBJECTS WHERE KEY = '%s' ;";
	public static String select2find_field_name_existance = "SELECT FIELD_NAME FROM OBJECTS WHERE KEY = '%s' AND FIELD_NAME = '%s' ; " ;

	public static LinkedList<String> makeQueries(
			QUERY_TYPE queryType,
			Map<String, String> data) {
		LinkedList<String> result = new LinkedList<String>();
			switch (queryType) {
			case INSERT:
				for(Map.Entry<String, String> entry: data.entrySet()){
					if(entry.getKey().startsWith("_")){ continue; } // don't use special fields 
					result.add(String.format(insert_format,
							data.get("_key"),
							entry.getKey(),
							entry.getValue()));
				}
				break;
			case UPDATE:
				for(Map.Entry<String, String> entry: data.entrySet()){
					if(entry.getKey().startsWith("_")){ continue; } // don't use special fields
					if(DB.objectHasFieldName(data.get("_key"), entry.getKey())){
						result.add(String.format(update_format,
								entry.getValue(),
								data.get("_key"),
								entry.getKey()));
					} else {
						result.add(String.format(insert_format,
								data.get("_key"),
								entry.getKey(),
								entry.getValue()));						
					}
				}
				break;
			case SELECT:
				// where #1
				String wherePart = null;
				if(data.containsKey("_key")){
					String key = data.get("_key");
					wherePart = " WHERE KEY = '" + key + "' ";
				} else {
					String objectName = data.get("_object");
					wherePart = " WHERE KEY LIKE '" + objectName + "%'" ; 
				}
				// where #2
				String wherePart2 = null;
				for(Map.Entry<String, String> entry: data.entrySet()){
					if(entry.getKey().startsWith("_")){ continue; } // don't use special fields 
					if(entry.getValue() != null && (!entry.getValue().isEmpty())){
						if(wherePart2 == null){
							wherePart2 = " (FIELD_NAME = '" + entry.getKey() + "' AND FIELD_VALUE " + entry.getValue() + ") ";
						} else {
							wherePart2 += " OR (FIELD_NAME = '" + entry.getKey() + "' AND FIELD_VALUE " + entry.getValue() + ") ";
						}
					}
				}
				if(wherePart2 != null){
					wherePart += " AND (" + wherePart2 + ") ";
				}
				result.add(String.format(select_format,	wherePart));
				// make select part
				break;
			case DELETE:
				result.add(String.format(delete_format,	data.get("_key")));
			default:
				break;
			}
		return (result);
	}
}
