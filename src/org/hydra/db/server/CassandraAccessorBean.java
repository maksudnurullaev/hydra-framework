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
		
		// tests path
		if(path == null 
				|| path.getErrorCode() != ERR_CODES.NO_ERROR
				|| path.getPathType() != PATH_TYPE.KSP___CF
				|| path._kspBean == null
				|| path._cfBean == null
				){
			getLog().error("Invalid access path!");
			return result;
		}
		
		String kspName = path.getPathPart(PARTS.P1_KSP);
		String cfName = path.getPathPart(PARTS.P2_CF);
		
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

		String formatStr = "\nGet column(s) for:\n" 
				+ "Keyspace: %s\n"
				+ "--> CF: %s\n" 
				+ "--> Key: %s\n" 
				+ "--> Super: %s";
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
		
		// try to insert batch
		Client client = clientBorrow();
		try {
			for(Map<String, Map<String, List<Mutation>>> batchMap:generateMutationMap(inPath, inBatchMap)){
				client.batch_mutate(inPath._kspBean.getName(), batchMap, ConsistencyLevel.ONE);
			}
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

	/**
	 * @param inPath
	 * @param inBatchMap
	 * @return {@code List<Map<KeyString, Map<ColumnFamilyString, List<Mutation>>>>}
	 */
	private List<Map<String, Map<String, List<Mutation>>>> generateMutationMap(
			CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inBatchMap) {
		
		List<Map<String, Map<String, List<Mutation>>>> result = new ArrayList<Map<String,Map<String,List<Mutation>>>>();
		
		getLog().debug(String.format("Generate mutation list for: %s, access path type: %s", inPath.getPath(), inPath.getPathType()));
		switch (inPath.getPathType()) {
		case KSP___CF:
			generateMutationMap4KspCf(inPath, inBatchMap, result);
			break;
		case KSP___CF___ID___LINKNAME:
			generateMutationMap4KspCfIDLinks(inPath, inBatchMap, result);			
			break;
		default:
			getLog().error(String.format("Unknow access path type to create mutation list for: %s, access path type: %s", inPath.getPath(), inPath.getPathType()));
			return null;
		}
		return result;
	}
	
	private void generateMutationMap4KspCfIDLinks(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inBatchMap,
			List<Map<String, Map<String, List<Mutation>>>> result) {
		// 1. setup access path for links
		CassandraVirtualPath linksVPath = new CassandraVirtualPath(inPath.getDescriptor(),
				String.format("%s.%s", inPath._kspBean.getName(), inPath._colBean.getName()));
		// 2. create batch data map for links
		generateMutationMap4KspCf(linksVPath, inBatchMap, result);
		// 3. create batch data map for LINKS table
	}

	private void generateMutationMap4KspCf(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inBatchMap, List<Map<String, Map<String, List<Mutation>>>> result) {
		getLog().debug("Generate mutation list for: " + inPath.getPath());
		
		Map<String, Map<String, List<Mutation>>> mapKeyMapCfListMutaion = new HashMap<String, Map<String,List<Mutation>>>();
		// * create Map<String, List<Mutation>> - mutationCfMap
		Map<String, List<Mutation>> mapCfListMutaion = new HashMap<String, List<Mutation>>();
		// ** create List<Mutation> - listOfMutation
		List<Mutation> listOfMutation = new ArrayList<Mutation>();
		for(Map.Entry<byte[], Map<byte[], byte[]>> mapIdColsVals:inBatchMap.entrySet()){
			// setup List<Column> object
			List<Column> listOfColumns = new ArrayList<Column>();			
			for(Map.Entry<byte[], byte[]> mapColVal:mapIdColsVals.getValue().entrySet()){
				listOfColumns.add(new Column(mapColVal.getKey(), mapColVal.getValue(), System.currentTimeMillis()));
			}
			// setup SuperColumn object
			SuperColumn superColumn = new SuperColumn();
			superColumn.setName(mapIdColsVals.getKey());
			superColumn.setColumns(listOfColumns);
			// setup ColumnOrSuperColumn object
			ColumnOrSuperColumn columnOrSuperColumn = new ColumnOrSuperColumn();
			columnOrSuperColumn.setSuper_column(superColumn);
			// setup Mutation object 
			Mutation mutation = new Mutation();
			mutation.setColumn_or_supercolumn(columnOrSuperColumn);
			// add to List<Mutation> result object
			listOfMutation.add(mutation);
			// make result
			result.add(mapKeyMapCfListMutaion);
		}
		// * setup mutationCfMap
		mapCfListMutaion.put(inPath._cfBean.getName(), listOfMutation);
		// setup mutationKeyCfMap
		mapKeyMapCfListMutaion.put(COLUMNS_KEY_DEF, mapCfListMutaion);
		// finish, make result
		result.add(mapKeyMapCfListMutaion);
		getLog().debug("Added super columns count: " + mapKeyMapCfListMutaion.size());
	}

	public void batchDelete4KspCf(CassandraVirtualPath path) {
		// tests
		if(path == null 
				|| path.getErrorCode() != ERR_CODES.NO_ERROR
				|| path.getPathType() != PATH_TYPE.KSP___CF){
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
					ColumnPath cpath = new ColumnPath(path._cfBean.getName());
					cpath.setSuper_column(columnOrSuperColumn.super_column.name);
					client.remove(path._kspBean.getName(), COLUMNS_KEY_DEF, cpath, System.currentTimeMillis(), ConsistencyLevel.ONE);
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
