package org.hydra.db.server;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.abstracts.ACassandraDescriptorBean;

//import org.hydra.utils.Result;


public class CassandraDescriptorBean extends ACassandraDescriptorBean {

	public ColumnBean getColumnBean(String keyspaceName,
			String columnFamilyName,
			String columnName) {	
				
		// 1. Check for keyspace
		if(keyspaceName == null ||
				!containsKeyspace(keyspaceName)){
			getLog().error("Validate failed! Could not find keyspace: " + keyspaceName);
			return null;
		}
		KeyspaceBean keyspace = getKeyspace(keyspaceName);
		
		// 2. Check for column family
		if(columnFamilyName == null ||
				keyspace.getColumnFamilyByName(columnFamilyName) == null){
			getLog().error(String.format("Validate failed! Could not find column family(%s) for keyspace(%s)!",
					columnFamilyName,
					keyspaceName));
			return null;			
		}
		ColumnFamilyBean columnFamily = keyspace.getColumnFamilyByName(columnFamilyName);
		
		// 3. Check for column
		if(columnName == null ||
				columnFamily.getColumnByName(columnName) == null){
			getLog().error(String.format(
					"Validate failed! Could not find column(%s) for column family(%s) and keyspace(%s)!",
					columnName,
					columnFamilyName,
					keyspaceName));
			return null;						
		}
		return columnFamily.getColumnByName(columnName);				
	}


}
