package org.hydra.tests.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.hydra.utils.DB;
import org.hydra.utils.DBUtils;

public class Just4Test2 {
	public static void main(String[] argv) {
		Connection c = DB.getConnection();
		System.out.println(c.toString());
		List<String> stmts = DBUtils.getDBObjectsCreateStatements();
		stmts.add("INSERT INTO Objects(KEY) VALUES('TEST');");
		stmts.add("SELECT * FROM OBJECTS;");
		try {
			for (String stmt : stmts) {
				PreparedStatement ps = c.prepareStatement(stmt);
				if (ps.execute()) {
					printRs(ps.getResultSet());
				}
			}
			c.commit();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
