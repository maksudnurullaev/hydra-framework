package org.hydra.db.server;

import org.hydra.db.server.abstracts.ACassandraDescriptorBean;
import org.hydra.utils.ResultAsMapOfStringAndColumnBean;

//import org.hydra.utils.Result;


public class CassandraDescriptorBean extends ACassandraDescriptorBean {

	public ResultAsMapOfStringAndColumnBean resultAsMapOfColumns(CassandraVirtualPath inPath) {
		ResultAsMapOfStringAndColumnBean result = new ResultAsMapOfStringAndColumnBean();
		
		if(inPath == null){
			getLog().error("Expected CassandraVirtualPath instead NULL!");
			
			result.setResult(false);
			result.setResult("Expected CassandraVirtualPath instead NULL!");
			return result; 
		}
		
		if(inPath._cfBean == null){
			getLog().error("Expected ColumnFamilyBean instead NULL!");
			
			result.setResult(false);
			result.setResult("Expected ColumnFamilyBean instead NULL!");
			return result; 
		}
		
		if(inPath._cfBean.columns.size() == 0){
			getLog().error("Columns size is 0!");
			result.setResult(false);
			result.setResult("Columns size is 0!");
			return result; 
		}
		
		result.setMapOfStringAndColumnBean(inPath._cfBean.columns);
		result.setResult(true);		
		return result;
	}	

}
