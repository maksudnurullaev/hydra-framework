package org.hydra.db.server;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.utils.ResultAsMapOfStringAndColumnBean;

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
		
		// 3. Check for column name
		if(columnName == null){
			getLog().error("Invalid column name: NULL!");
			return null;
		}		

		
		if(columnFamily.getAnyColumnOrLinkByName(columnName) == null){
			getLog().error("Could not find any column or link for name:" + columnName);
			return null;						
		}
		
		return columnFamily.getAnyColumnOrLinkByName(columnName);				
	}

	public ResultAsMapOfStringAndColumnBean resultAsMapOfColumns(CassandraVirtualPath inPath) {
		ResultAsMapOfStringAndColumnBean result = new ResultAsMapOfStringAndColumnBean();
		
		if(inPath == null){
			getLog().error("Expected CassandraVirtualPath instead NULL!");
			
			result.setResult(false);
			result.setResult("Expected CassandraVirtualPath instead NULL!");
			return result; 
		}
		
		if(inPath.cfBean == null){
			getLog().error("Expected ColumnFamilyBean instead NULL!");
			
			result.setResult(false);
			result.setResult("Expected ColumnFamilyBean instead NULL!");
			return result; 
		}
		
		if(inPath.cfBean.getColumns().size() == 0){
			getLog().error("Columns size is 0!");
			result.setResult(false);
			result.setResult("Columns size is 0!");
			return result; 
		}
		
		result.setMapOfStringAndColumnBean(inPath.cfBean.getColumns());
		result.setResult(true);		
		return result;
	}	
	
	public ResultAsMapOfStringAndColumnBean resultAsMapOfLinks(CassandraVirtualPath inPath) {
		ResultAsMapOfStringAndColumnBean result = new ResultAsMapOfStringAndColumnBean();
		
		if(inPath == null){
			getLog().error("Expected CassandraVirtualPath instead NULL!");
			
			result.setResult(false);
			result.setResult("Expected CassandraVirtualPath instead NULL!");
			return result; 
		}
		
		if(inPath.cfBean == null){
			getLog().error("Expected ColumnFamilyBean instead NULL!");
			
			result.setResult(false);
			result.setResult("Expected ColumnFamilyBean instead NULL!");
			return result; 
		}
		
		if(inPath.cfBean.getLinks().size() == 0){
			getLog().error("Columns size is 0!");
			result.setResult(false);
			result.setResult("Columns size is 0!");
			return result; 
		}
		
		result.setMapOfStringAndColumnBean(inPath.cfBean.getLinks());
		result.setResult(true);		
		return result;
	}	
}
