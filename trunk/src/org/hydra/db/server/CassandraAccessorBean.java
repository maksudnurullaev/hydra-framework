package org.hydra.db.server;

import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.utils.Constants;

public class CassandraAccessorBean extends ACassandraAccessor {
	public List<Column> getDBColumns(CassandraVirtualPath path){
		//return getDBColumns(path.ksp, path.cf, path.key, path.col);
		return null;
	}
	
	public List<Column> getDBColumns(String keyspaceName,
			String columnFamilyName,
			String keyID,
			String superName) {

		// debug inforation
		String formatStr = "\nGet column(s) for:\n"
			+ " Keyspace: %s\n"
			+ "       CF: %s\n"
			+ "      Key: %s\n"
			+ "    Super: %s";
		getLog().debug(String.format(formatStr,
				keyspaceName,
				columnFamilyName,
				keyID,
				superName));
		getLog().debug("\n" + String.format(CassandraDescriptorBean.PATH2COLUMN4,
				keyspaceName,
				columnFamilyName,
				keyID,
				superName));
		
        // Setup column range
        SlicePredicate predicate = new SlicePredicate();
        
        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(Constants.string2UTF8Bytes(superName));            
        sliceRange.setFinish(Constants.string2UTF8Bytes(superName));
        
        predicate.setSlice_range(sliceRange);        	
                
        // Setup column family
        ColumnParent parent = new ColumnParent(columnFamilyName);

        getLog().debug("Borrow client...");
        Cassandra.Client cClient = clientBorrow();
        
		List<Column> result = null;		
        try {
			List<ColumnOrSuperColumn> results = cClient.get_slice(
					keyspaceName, 
					keyID,
					parent, 
					predicate, 
					ConsistencyLevel.ONE);
			
			getLog().debug("Get getDBObject results: " + results.size());
			
			if(results.size() == 0){
				getLog().debug("No result!");
				result = null;		
			}else if(results.size() == 1){ // Correct!!!			
				result = results.get(0).super_column.columns;
			}else{
				getLog().debug("Anomal count of results!");
				result = null;						
			}
			
		} catch (Exception e) {
			getLog().equals(e.getMessage());
			e.printStackTrace();			
			result = null;			
		}finally{
			clientRelease(cClient);
			getLog().debug("Borrowed client closed!");
		}
		
		return result;
	}	
	
}
