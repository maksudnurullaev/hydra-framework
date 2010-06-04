package org.hydra.db.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;

public class CassandraAccessorBean extends ACassandraAccessor {
	public ResultAsListOfColumnOrSuperColumn resultAsListOfColumns4KspCf(CassandraVirtualPath path) {
		ResultAsListOfColumnOrSuperColumn result = new ResultAsListOfColumnOrSuperColumn();
		String kspName = path.getPathPart(PARTS.KSP);
		String cfName = path.getPathPart(PARTS.CF);
		
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
			result.setColumnOrSuperColumn(client.get_slice(kspName, COLUMNS_KEY_DEF, cf, predicate, ConsistencyLevel.ONE));
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			result.setResult(e.getMessage());
		}finally{
			clientRelease(client);
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
		sliceRange.setStart(DBUtils.string2UTF8Bytes(supe_r));
		sliceRange.setFinish(DBUtils.string2UTF8Bytes(supe_r));

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

	public Result batchMutate(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[],byte[]>> inBatchMap) {
		    /*  ID          col    val                   */
		Result result = new Result();
		
		// some tests
		if(inBatchMap == null ||
				inBatchMap.size() == 0){
			result.setResult(false);
			result.setObject("Invalid batch map!");
			getLog().warn("Invalid batch map!");
			return result;
		}		
		
		/**
		 * preparing objects for 
		 * 	batch_mutate(
		 * 		String keyspace, 
		 * 		Map<String, Map<String, List<Mutation>>> mutation_map, 
		 * 		ConsistencyLevel consistency_level
		 * 	)
		 */
		// create Map<String, Map<String, List<Mutation>>> - mutationKeyCfMap
		Map<String, Map<String, List<Mutation>>> mutationKeyCfMap = new HashMap<String, Map<String,List<Mutation>>>();
		// * create Map<String, List<Mutation>> - mutationCfMap
		Map<String, List<Mutation>> mutationCfMap = new HashMap<String, List<Mutation>>();
		// ** create List<Mutation> - listOfMutation
		List<Mutation> listOfMutation = new ArrayList<Mutation>();
		for(Map.Entry<byte[], Map<byte[], byte[]>> mapIdColsVals:inBatchMap.entrySet()){			
			// *** create Mutaion
			Mutation mutation = new Mutation();
			// **** create ColumnOrSuperColumn
			ColumnOrSuperColumn columnOrSuperColumn = new ColumnOrSuperColumn();
			// ***** create SuperColumn
			SuperColumn superColumn = new SuperColumn();
			superColumn.setName(mapIdColsVals.getKey());
			// ****** create & init List<Column>
			List<Column> listOfColumns = new ArrayList<Column>();			
			for(Map.Entry<byte[], byte[]> mapColVal:mapIdColsVals.getValue().entrySet()){
				listOfColumns.add(new Column(mapColVal.getKey(), mapColVal.getValue(), System.currentTimeMillis()));
			}
			// ***** setup SuperColumn
			superColumn.setColumns(listOfColumns);
			// **** setup ColumnOrSuperColumn
			columnOrSuperColumn.setSuper_column(superColumn);
			// *** setup Mutation
			mutation.setColumn_or_supercolumn(columnOrSuperColumn);
			// ** Finish - add to mutaions list
			listOfMutation.add(mutation);
		}
		
		// * setup mutationCfMap
		mutationCfMap.put(inPath.cfBean.getName(), listOfMutation);
		// setup mutationKeyCfMap
		mutationKeyCfMap.put(COLUMNS_KEY_DEF, mutationCfMap);
		
		// try to insert batch
		Client client = clientBorrow();
		try {
			client.batch_mutate(inPath.kspBean.getName(), mutationKeyCfMap, ConsistencyLevel.ONE);
			result.setResult(true);
			result.setResult("Batch mutate accepted!");
			getLog().debug("Batch mutate accepted!");
		} catch (Exception e) {
			result.setResult(false);
			result.setResult(e.getMessage());
			getLog().error(e.getMessage());
		}finally{
			clientRelease(client);
		}		
		return result;
	}

	public void batchDelete4KspCf(CassandraVirtualPath path) {
		// tests
		if(path == null 
				|| path.getErrorCode() != ERR_CODES.NO_ERROR
				|| path.getPathType() != PATH_TYPE.KSP___CF___){
			getLog().error("Invalid access path!");
			return;
		}
		
		ResultAsListOfColumnOrSuperColumn result = resultAsListOfColumns4KspCf(path);
		
		// test for result
		if(!result.isOk()){
			getLog().error("Invalid result:" + result.getResult());
			return;			
		}
		
		// test for attache object (should be List)
		if(result.getColumnOrSuperColumn() == null ||
				result.getColumnOrSuperColumn().size() == 0){
			getLog().error("Invalid result: NULL or not EMPTY!");
			return;						
		}
				
		Client client = clientBorrow();
		try{
			for(ColumnOrSuperColumn columnOrSuperColumn: result.getColumnOrSuperColumn()){
				if(columnOrSuperColumn.getSuper_column() != null){
					ColumnPath cpath = new ColumnPath(path.cfBean.getName());
					cpath.setSuper_column(columnOrSuperColumn.super_column.name);
					client.remove(path.kspBean.getName(), COLUMNS_KEY_DEF, cpath, System.currentTimeMillis(), ConsistencyLevel.ONE);
				}else{
					getLog().error("Delete object should be SuperColumn");
				}
			}
		}catch (Exception e) {
			getLog().error(e.getMessage());
		}finally{
			clientRelease(client);
		}
	}

}
