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
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;

public class CassandraAccessorBean extends ACassandraAccessor {	
	public ResultAsListOfColumnOrSuperColumn get4KspCfId(
			CassandraVirtualPath inPath) {
		ResultAsListOfColumnOrSuperColumn result = new ResultAsListOfColumnOrSuperColumn();		
		// test access path
		Result tempResult = DBUtils.validate4NullPathKspCfPathType(inPath, PATH_TYPE.KSP___CF___ID);
		if(!tempResult.isOk()){
			result.setResult(false);
			result.setResult(tempResult.getResult());
			return result;
		}
		// debug
		getLog().debug(String.format("Try to get data for ksp(%s), cf(%s), key(%s)...", 
				inPath.getPathPart(PARTS.P1_KSP), 
				inPath.getPathPart(PARTS.P2_CF),
				inPath.getPathPart(PARTS.P3_KEY)));
		// setup ColumnParent 
		ColumnParent cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
		// setup slice range
        SlicePredicate predicate = DBUtils.getSlicePredicate(inPath.getPathPart(PARTS.P3_KEY), inPath.getPathPart(PARTS.P3_KEY));		
        // borrow cassandra's client 		
        getLog().debug("Try to get data for: " + inPath.getPath());
		try2GetResultAsListOfColumnOrSuperColumn(result,
				inPath.getPathPart(PARTS.P1_KSP), 
				COLUMNS_KEY_DEF, 
				cf, 
				predicate, 
				ConsistencyLevel.ONE);
		// finish
		return result;
	}	
	
	public ResultAsListOfColumnOrSuperColumn get4KspCf(CassandraVirtualPath inPath) {
		ResultAsListOfColumnOrSuperColumn result = new ResultAsListOfColumnOrSuperColumn();		
		// test access path
		Result tempResult = DBUtils.validate4NullPathKspCfPathType(inPath, PATH_TYPE.KSP___CF);
		if(!tempResult.isOk()){
			result.setResult(false);
			result.setResult(tempResult.getResult());
			return result;
		}
		getLog().debug(String.format("Try to get data for ksp(%s), cf(%s), key(COLUMNS)...", 
				inPath.getPathPart(PARTS.P1_KSP), 
				inPath.getPathPart(PARTS.P2_CF)));
		// setup ColumnParent 
		ColumnParent cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
		// setup slice range
        SlicePredicate predicate = DBUtils.getSlicePredicate(null, null);		
        // borrow cassandra's client 		
		try2GetResultAsListOfColumnOrSuperColumn(result,
				inPath.getPathPart(PARTS.P1_KSP), 
				COLUMNS_KEY_DEF, 
				cf, 
				predicate, 
				ConsistencyLevel.ONE);
		// finish
		return result;
	}

	public void try2GetResultAsListOfColumnOrSuperColumn(ResultAsListOfColumnOrSuperColumn inResult,
			String inKsp,
			String inKey,
			ColumnParent inCf,
			SlicePredicate inPredicate,
			ConsistencyLevel inConsistencyLevel){
        // borrow cassandra's client 		
        getLog().debug("Borrow client...");
		Cassandra.Client client = clientBorrow();
		try {
			inResult.setColumnOrSuperColumn(client.get_slice(inKsp,	inKey, inCf, inPredicate, inConsistencyLevel));
			inResult.setResult(true);
		} catch (Exception e) {
			inResult.setResult(false);
			getLog().error(e.toString());
			inResult.setResult(e.toString());
		}finally{
			clientRelease(client);
		}
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
//		case KSP___CF___ID:
//			getLog().error("generateMutationMap4KspCf(inPath, inBatchMap, result);");
//			break;
////			generateMutationMap4KspCfId(inPath, inBatchMap, result);
////			break;
		case KSP___CF___ID___LINKNAME:
			generateMutationMap4KspCfIDLinks(inPath, inBatchMap, result);			
			break;
		default:
			getLog().error(String.format("Unknow access path type to create mutation list for: %s, access path type: %s", inPath.getPath(), inPath.getPathType()));
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

	public Result delete4KspCf(CassandraVirtualPath inPath) {
		// test incoming path
		Result result = DBUtils.validate4NullPathKspCfPathType(inPath, PATH_TYPE.KSP___CF);
		if(!result.isOk()){
			return result;
		}
		// remove full COLUMNS data
		Client client = clientBorrow();
		
		ColumnPath cf = new ColumnPath(inPath._cfBean.getName());
		try {
			client.remove(inPath.getPathPart(PARTS.P1_KSP), 
					COLUMNS_KEY_DEF, 
					cf, 
					System.currentTimeMillis(), 
					ConsistencyLevel.ONE);
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			result.setResult(e.toString());
		}finally{
			clientRelease(client);
		}
		return result;
	}
	
//	public Result delete4KspCfId(CassandraVirtualPath inPath) {
//		// get all records
//		ResultAsListOfColumnOrSuperColumn result = get4KspCf(inPath);
//		if(!result.isOk()){
//			getLog().error("Invalid result:" + result.getResult());
//			return result;			
//		}
//		
//		// test for attache object (should be List)
//		if(result.getColumnOrSuperColumn() == null ||
//				result.getColumnOrSuperColumn().size() == 0){
//			result.setResult("Invalid result: NULL or not EMPTY!");
//			result.setResult(false);
//			getLog().error("Invalid result: NULL or not EMPTY!");
//			return result;
//		}
//				
//		Client client = clientBorrow();
//		//TODO Should be optimazed by one operation - delete key = COLUMNS!!!
//		try{
//			for(ColumnOrSuperColumn columnOrSuperColumn: result.getColumnOrSuperColumn()){
//				if(columnOrSuperColumn.getSuper_column() != null){
//					ColumnPath cpath = new ColumnPath(inPath._cfBean.getName());
//					cpath.setSuper_column(columnOrSuperColumn.super_column.name);
//					client.remove(inPath.getPathPart(PARTS.P1_KSP), 
//							COLUMNS_KEY_DEF, 
//							cpath, 
//							System.currentTimeMillis(), 
//							ConsistencyLevel.ONE);
//				}else{
//					getLog().error("Delete object should be SuperColumn");
//				}
//			}
//			result.setResult(true);
//		}catch (Exception e) {
//			result.setResult(e.toString());
//			result.setResult(false);
//			getLog().error(e.toString());
//		}finally{
//			clientRelease(client);
//		}
//		return result;
//	}

	public Result delete4KspCfId(CassandraVirtualPath inPath) {
		// tests path
		Result result = DBUtils.validate4NullPathKspCfPathType(inPath, PATH_TYPE.KSP___CF___ID);

		if(!result.isOk()){
			getLog().error(result.getResult());
			return result;
		}
				
		Client client = clientBorrow();
		try{
			ColumnPath cpath = new ColumnPath(inPath.getPathPart(PARTS.P2_CF));
			cpath.setSuper_column(DBUtils.string2UTF8Bytes(inPath.getPathPart(PARTS.P3_KEY)));
			client.remove(inPath.getPathPart(PARTS.P1_KSP), 
					COLUMNS_KEY_DEF, 
					cpath, 
					System.currentTimeMillis(), 
					ConsistencyLevel.ONE);
			result.setResult(true);
			result.setResult(null);
		}catch (Exception e) {
			getLog().error(e.getMessage());
			result.setResult(false);
			result.setResult(e.toString());			
		}finally{
			clientRelease(client);
		}		
		
		return result;
	}

}
