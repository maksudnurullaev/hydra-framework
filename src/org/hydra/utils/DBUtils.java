package org.hydra.utils;

import java.util.ArrayList;
import java.util.List;

public final class DBUtils {

	public static List<String> getDBObjectsCreateStatements() {
		List<String> stmts = new ArrayList<String>();
		stmts.add("CREATE TABLE Objects (ID INT PRIMARY KEY AUTO_INCREMENT, KEY VARCHAR(64), FIELD_NAME VARCHAR(64), FIELD_ORDER INT DEFAULT 0 NOT NULL, VALUE VARCHAR(255));");
		stmts.add("CREATE INDEX iKEY ON Objects(KEY);");
		stmts.add("CREATE INDEX iFIELD_NAME ON Objects(FIELD_NAME);");
		stmts.add("CREATE INDEX iFIELD_ORDER ON Objects(FIELD_ORDER);");
		return(stmts);
	}

}
