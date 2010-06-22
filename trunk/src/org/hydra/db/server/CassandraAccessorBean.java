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
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;

public class CassandraAccessorBean extends ACassandraAccessor {	
	public ResultAsListOfColumnOrSuperColumn find(
			CassandraVirtualPath inPath) {
		// debug
		getLog().debug("Try to get data for path: " + inPath.getPath());
		// init new result
		ResultAsListOfColumnOrSuperColumn result = new ResultAsListOfColumnOrSuperColumn();		
		// test for NULLs
		Result tempResult = DBUtils.test4NullKspCf(inPath);
		if(!tempResult.isOk()){
			result.setResult(false);
			result.setResult(tempResult.getResult());
			return result;
		}		
		// init parameters
		String ksp = null;
		String key = null;
		ColumnParent cf = null;
		SlicePredicate predicate = null;
		ConsistencyLevel cLevel = null;
		
		switch (inPath.getPathType()) {
		case KSP___CF:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
			key = COLUMNS_KEY_DEF;
			predicate = DBUtils.getSlicePredicate(null, null);
			cLevel = ConsistencyLevel.ONE;
			break;
		case KSP___CF___ID:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
			key = COLUMNS_KEY_DEF;			
			predicate = DBUtils.getSlicePredicate(inPath.getPathPart(PARTS.P3_KEY), inPath.getPathPart(PARTS.P3_KEY));	
			cLevel = ConsistencyLevel.ONE;
			break;
		default:
			String errStr = String.format("Unknow path(%s) to get db records!", inPath.getPath());
			result.setResult(false);
			result.setResult(errStr);
			getLog().error(errStr);
			return result;
		}
		
		// try to get records
		Cassandra.Client client = clientBorrow();
		try {
			result.setColumnOrSuperColumn(client.get_slice(ksp,	key, cf, predicate, cLevel));
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			getLog().error(e.toString());
			result.setResult(e.toString());
		}finally{
			clientRelease(client);
		}
		return result;
	}	

	public Result update(CassandraVirtualPath inPath,
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
		case KSP___CF___ID:
			generateMutationMap4KspCf(inPath, inBatchMap, result);
			break;
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

	public Result delete(CassandraVirtualPath inPath) {
		// tests path
		Result result = DBUtils.test4NullKspCf(inPath);

		if(!result.isOk()){
			getLog().error(result.getResult());
			return result;
		}
		// init params
		String ksp = inPath.getPathPart(PARTS.P1_KSP);
		String key = COLUMNS_KEY_DEF;
		ColumnPath cf = new ColumnPath(inPath.getPathPart(PARTS.P2_CF));
		long timestamp = System.currentTimeMillis();
		ConsistencyLevel cLevel =ConsistencyLevel.ONE;
		
		switch (inPath.getPathType()) {
		case KSP___CF:
			break;
		case KSP___CF___ID:
			cf.setSuper_column(DBUtils.string2UTF8Bytes(inPath.getPathPart(PARTS.P3_KEY)));
			break;
		default:
			String errStr = String.format("Unknow path(%s) to get db records!", inPath.getPath());
			result.setResult(false);
			result.setResult(errStr);
			getLog().error(errStr);
			return result;
		}		
		
		Client client = clientBorrow();
		try{
			client.remove(ksp, 
					key, 
					cf, 
					timestamp, 
					cLevel);
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
