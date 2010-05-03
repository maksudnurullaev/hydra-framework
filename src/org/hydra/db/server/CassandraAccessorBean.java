package org.hydra.db.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.hydra.db.beans.AccessPath;
import org.hydra.db.beans.CFKey.TYPE;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.utils.Constants;



public class CassandraAccessorBean extends ACassandraAccessor {
	private List<Column> getDBColumns(AccessPath accessPath) {
		List<Column> result = null;
		
        SlicePredicate predicate = new SlicePredicate();
        
        //if(accessPath.getType() == TYPE.COLUMNS)
        //{
        //	List<byte[]> column_names = new ArrayList<byte[]>();
        //    predicate.setColumn_names(column_names);
        //}//else{
            SliceRange sliceRange = new SliceRange();
            //sliceRange.setStart(new byte[0]);
            sliceRange.setStart(Constants.string2UTF8Bytes(accessPath.getCfKey()));            
            sliceRange.setFinish(new byte[0]);
            predicate.setSlice_range(sliceRange);        	
        //}
        
        
        ColumnParent parent = new ColumnParent(accessPath.getCfName());

        getLog().debug("Borrow client...");
        Cassandra.Client cClient = clientGet();
        try {
			List<ColumnOrSuperColumn> results = cClient.get_slice(
					accessPath.getKsName(), 
					accessPath.getScfKey(),
					parent, 
					predicate, 
					ConsistencyLevel.ONE);
			
			getLog().debug("Get getDBObject results: " + results.size());
			
			if(results.size() == 0){
				accessPath.setValue("No columns found!");
			}else{			
				result = results.get(0).super_column.columns;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// Some error
			accessPath.setValue(e.getMessage());
			
			result = null;			
		}finally{
			clientClose(cClient);
			getLog().debug("Borrowed client closed!");
		}
		
		return result;
	}

	public List<Column> getColumns(AccessPath accessPath) {
		return getDBColumns(accessPath);
	}

}
