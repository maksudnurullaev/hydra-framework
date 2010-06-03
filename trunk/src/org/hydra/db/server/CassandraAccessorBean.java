package org.hydra.db.server;

import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;

public class CassandraAccessorBean extends ACassandraAccessor {
	public Result getDBColumns(CassandraVirtualPath inPath) {

		// Check for access path
		if (inPath == null) {
			Result result = new Result();
			result.setResult(false);
			result.setResult("Access path is NULL!");
			return result;
		} else if (inPath.getErrorCode() != ERR_CODES.NO_ERROR) {
			Result result = new Result();
			result.setResult(false);
			result.setResult("Access path is invalid: " + inPath._errString);
			return result;
		}

		// Main action
		switch (inPath.getResultType()) {
		case LIST_OF_IDS4KSP_CF:
			return resultAsListOfIDs4KspCf(inPath);
		case MAP4KSP_CF_COLUMNS:
			return resultAsMapOfColumns(inPath);
		case MAP4KSP_CF_LINKS:
			return resultAsMapOfLinks(inPath);
		default:
			Result result = new Result();
			result.setResult(false);
			result.setResult("Undefined result type: " + inPath.getResultType());
			return result;
		}
	}

	private Result resultAsMapOfColumns(CassandraVirtualPath inPath) {
		Result result = new Result();
		
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
		
		result.setObject(inPath.cfBean.getColumns());
		result.setResult(true);		
		return result;
	}	
	
	private Result resultAsMapOfLinks(CassandraVirtualPath inPath) {
		Result result = new Result();
		
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
		
		result.setObject(inPath.cfBean.getLinks());
		result.setResult(true);		
		return result;
	}	

	private Result resultAsListOfIDs4KspCf(CassandraVirtualPath path) {
		Result result = new Result();
		String kspName = path.getPathPart(PARTS.KSP);
		String cfName = path.getPathPart(PARTS.CF);
		String keyName = "COLUMNS";
		
		getLog().debug(String.format("Get IDs for ksp(%s), cf(%s), key(COLUMNS)...", kspName, cfName));
		
		// setup ColumnParent 
		ColumnParent cf = new ColumnParent(cfName);
		
		// setup slice range
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(new byte[0]);
        sliceRange.setFinish(new byte[0]);
        predicate.setSlice_range(sliceRange);
		
        // borrow cassandra's client 		
        getLog().debug("Borrow client...");
		Cassandra.Client client = clientBorrow();
		
		
		try {
			result.setObject(client.get_slice(kspName, keyName, cf, predicate, ConsistencyLevel.ONE));
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			result.setResult(e.getMessage());
		}

		// finish
		return result;
	}

	public List<Column> getDBColumns(String keyspaceName,
			String cf, String key, String supe_r) {

		String formatStr = "\nGet column(s) for:\n" + " Keyspace: %s\n"
				+ "       CF: %s\n" + "      Key: %s\n" + "    Super: %s";
		getLog().debug(
				String.format(formatStr, keyspaceName, cf, key,
						supe_r));
		getLog().debug(
				"\n"
						+ String.format(CassandraDescriptorBean.PATH2COLUMN4,
								keyspaceName, cf, key,
								supe_r));

		// Setup column range
		SlicePredicate predicate = new SlicePredicate();

		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(Constants.string2UTF8Bytes(supe_r));
		sliceRange.setFinish(Constants.string2UTF8Bytes(supe_r));

		predicate.setSlice_range(sliceRange);

		// Setup column family
		ColumnParent parent = new ColumnParent(cf);

		getLog().debug("Borrow client...");
		Cassandra.Client client = clientBorrow();

		List<Column> result = null;
		try {
			List<ColumnOrSuperColumn> results = client.get_slice(keyspaceName,
					key, parent, predicate, ConsistencyLevel.ONE);

			getLog().debug("Get getDBObject results: " + results.size());

			if (results.size() == 0) {
				getLog().debug("No result!");
				result = null;
			} else if (results.size() == 1) { // Correct!!!
				result = results.get(0).super_column.columns;
			} else {
				getLog().debug("Anomal count of results!");
				result = null;
			}

		} catch (Exception e) {
			getLog().equals(e.getMessage());
			e.printStackTrace();
			result = null;
		} finally {
			clientRelease(client);
			getLog().debug("Borrowed client closed!");
		}

		return result;
	}

}
