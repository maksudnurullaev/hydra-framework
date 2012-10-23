package org.hydra.tests.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.hydra.utils.DBUtils;
import org.hydra.utils.DBUtils.QUERY_TYPE;

public class Just4Test2 {
	public static void main(String[] argv) {
		
		Map<String, String> data = new HashMap<String, String>();
//		data.put("_object", "version");
		data.put("_key", "version 2012.10.23 10:31:47 587 97 8a");
//		data.put("name", "='value11'");
//		data.put("name2", "like 'value2%'");
//		data.put("name3", "='test");
		LinkedList<String> queries = DBUtils.makeQueries(QUERY_TYPE.DELETE, data);
		while(!queries.isEmpty()){
			System.out.println(queries.removeFirst());
		}
//		List<String> stmts = DBUtils.getDBObjectsCreateStatements();
//		stmts.clear();
//		stmts.add("INSERT INTO Objects(KEY) VALUES('TEST');");
//		stmts.add("SELECT * FROM OBJECTS;");
//		try {
//			for (String stmt : stmts) {
//				PreparedStatement ps = c.prepareStatement(stmt);
//				if (ps.execute()) {
//					printRs(ps.getResultSet());
//				}
//			}
//			c.commit();
//			c.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	static void printRs(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();

			System.out.println("");

			int numberOfColumns = rsmd.getColumnCount();

			for (int i = 1; i <= numberOfColumns; i++) {
				if (i > 1)
					System.out.print(",  ");
				String columnName = rsmd.getColumnName(i);
				System.out.print(columnName);
			}
			System.out.println("");

			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue);
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
