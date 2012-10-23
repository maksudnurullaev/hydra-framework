package org.hydra.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.utils.DBUtils.QUERY_TYPE;

public final class DB {
	private static Log _log = LogFactory.getLog("org.hydra.utils.DB");

	public static boolean setObject(QUERY_TYPE queryType, Map<String, String> data) {
		Connection connection = DBUtils.getConnection();
		try {
			LinkedList<String> queries = DBUtils.makeQueries(queryType, data);
			while(!queries.isEmpty()){
				connection.prepareStatement(queries.remove()).execute();
			}
		} catch (SQLException e) {
			_log.error(e.toString());
			return(false);
		}
		return(true);
	}

	public static void getObjects(String inAppID, String objectName) {
		// TODO Auto-generated method stub
		
	}
	
	public static Map<String, Map<String, String>> getObject(Map<String, String> data) {
		LinkedList<String> listOfQueries= DBUtils.makeQueries(QUERY_TYPE.SELECT, data);
		if(!listOfQueries.isEmpty()){
			Connection connection = DBUtils.getConnection();
			ResultSet rs;
			try {
				Statement statement = connection.createStatement();
				rs = statement.executeQuery(listOfQueries.remove());
				Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
				while(rs.next()){
					String key = rs.getString(1);
					String name = rs.getString(2);
					String value = rs.getString(3);
					int order = rs.getInt(4);
					if(!result.containsKey(key)){ result.put(key, new HashMap<String, String>()); }
					if(!result.get(key).containsKey(name)){
						result.get(key).put(name, value);						
					} else {
						result.get(key).put(name, result.get(key).get(name) + value);
					}
				}
				if(result.size() == 0) { return(null); }
				return(result);
			} catch (SQLException e) {
				_log.error(e.toString());
				return(null);
			}
		}
		return(null);
	}

	public static boolean deleteObject(Map<String, String> data) {
		LinkedList<String> listOfQueries= DBUtils.makeQueries(QUERY_TYPE.DELETE, data);
		if(!listOfQueries.isEmpty()){
			Connection connection = DBUtils.getConnection();
			try {
				Statement statement = connection.createStatement();
				statement.execute(listOfQueries.remove());
			} catch (SQLException e) {
				_log.error(e.toString());
				return(false);
			}
		}
		return(true);
	}

	public static boolean objectHasFieldName(String key, String fieldName) {
		Connection connection = DBUtils.getConnection();
		ResultSet rs;
		try {
			Statement statement = connection.createStatement();
			rs = statement.executeQuery(String.format(DBUtils.select2find_field_name_existance, key, fieldName));
			return(rs.next());
		} catch (SQLException e) {
			_log.error(e.toString());
		}
		return(false);		
	}

}
