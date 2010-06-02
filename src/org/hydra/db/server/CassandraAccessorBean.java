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
	public Result getDBColumns(CassandraVirtualPath path) {

		// Check for access path
		if (path == null) {
			Result result = new Result();
			result.setResult(false);
			result.setResult("Access path is NULL!");
			return result;
		} else if (path.getErrorCode() != ERR_CODES.NO_ERROR) {
			Result result = new Result();
			result.setResult(false);
			result.setResult("Access path is invalid: " + path._errString);
			return result;
		}

		// Main action
		switch (path.getResultType()) {
		case LIST_OF_IDS4KSP_CF:
			return listOfIDs4KspCf(path);
		default:
			Result result = new Result();
			result.setResult(false);
			result.setResult("Undefined result type:" + path.getResultType());
			return result;
		}
	}

	private Result listOfIDs4KspCf(CassandraVirtualPath path) {
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
