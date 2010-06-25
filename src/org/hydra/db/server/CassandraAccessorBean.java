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
import org.hydra.db.utils.DeletePack;
import org.hydra.utils.Constants;
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
			key = KEY_COLUMNS_DEF;
			predicate = DBUtils.getSlicePredicate(null, null);
			cLevel = ConsistencyLevel.ONE;
			break;
		case KSP___CF___ID:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
			key = KEY_COLUMNS_DEF;			
			predicate = DBUtils.getSlicePredicate(inPath.getPathPart(PARTS.P3_KEY), inPath.getPathPart(PARTS.P3_KEY));	
			cLevel = ConsistencyLevel.ONE;
			break;
		case KSP___CF___ID___LINKNAME:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath._kspBean.getLinkTableName());
			key = inPath.getPathPart(PARTS.P3_KEY);
			predicate = DBUtils.getSlicePredicate(inPath.getPathPart(PARTS.P4_SUPER), inPath.getPathPart(PARTS.P4_SUPER));	
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
		
		// setup access path for links
		CassandraVirtualPath linksVPath = new CassandraVirtualPath(inPath.getDescriptor(),
				String.format("%s.%s", inPath._kspBean.getName(), inPath._cfLinkBean.getName()));
		// create mutation
		List<byte[]> linkIDs = generateMutationMap4KspCf(linksVPath, inBatchMap, result);
		// create mutation for links
		generateMutation4Links(inPath, linkIDs, result);
	}

	private void generateMutation4Links(CassandraVirtualPath inPath,
			List<byte[]> linkIDs,
			List<Map<String, Map<String, List<Mutation>>>> inResult) {
		
		Map<String, Map<String, List<Mutation>>> mapKeyMapCfListMutaion = new HashMap<String, Map<String,List<Mutation>>>();
		Map<String, List<Mutation>> mapCfListMutaion = new HashMap<String, List<Mutation>>();
		List<Mutation> listOfMutation = new ArrayList<Mutation>();
		
		List<Column> listOfColumns = new ArrayList<Column>();
		
		// generate columns
		for(byte[] bytes: linkIDs){
			listOfColumns.add(new Column(bytes, DBUtils.string2UTF8Bytes(Constants.GetCurrentDateTime()), System.currentTimeMillis()));
		}
		
		// generate super_column
		SuperColumn superColumn = new SuperColumn();
		superColumn.setName(DBUtils.string2UTF8Bytes(inPath._cfLinkBean.getName()));
		superColumn.setColumns(listOfColumns);
		
		// generate ColumnOrSuperColumn
		ColumnOrSuperColumn columnOrSuperColumn = new ColumnOrSuperColumn();
		columnOrSuperColumn.setSuper_column(superColumn);
		
		// generate Mutation
		Mutation mutation = new Mutation();
		mutation.setColumn_or_supercolumn(columnOrSuperColumn);
		listOfMutation.add(mutation);		
		
		// set Cf
		mapCfListMutaion.put(inPath._kspBean.getLinkTableName(), listOfMutation);
		
		// set Key
		mapKeyMapCfListMutaion.put(inPath.getID(), mapCfListMutaion);		
		
		// update result
		inResult.add(mapKeyMapCfListMutaion);		
	}

	private List<byte[]> generateMutationMap4KspCf(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inBatchMap, List<Map<String, Map<String, List<Mutation>>>> inResult) {
		getLog().debug("Generate mutation list for: " + inPath.getPath());
		
		List<byte[]> resultListOfIDs = new ArrayList<byte[]>();
		
		Map<String, Map<String, List<Mutation>>> mapKeyMapCfListMutaion = new HashMap<String, Map<String,List<Mutation>>>();
		Map<String, List<Mutation>> mapCfListMutaion = new HashMap<String, List<Mutation>>();
		List<Mutation> listOfMutation = new ArrayList<Mutation>();
		//            Super       Col     Val
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
			inResult.add(mapKeyMapCfListMutaion);
			// add ids of mutation
			resultListOfIDs.add(mapIdColsVals.getKey());
		}
		
		// set Cf
		mapCfListMutaion.put(inPath._cfBean.getName(), listOfMutation);
		
		// set Key
		mapKeyMapCfListMutaion.put(KEY_COLUMNS_DEF, mapCfListMutaion);
		
		// update result
		inResult.add(mapKeyMapCfListMutaion);
		
		getLog().debug("Added super columns count: " + mapKeyMapCfListMutaion.size());
		
		// finish
		return resultListOfIDs;
	}

	public Result delete(CassandraVirtualPath inPath) {
		// tests path
		Result result = DBUtils.test4NullKspCf(inPath);

		if(!result.isOk()){
			getLog().error(result.getResult());
			return result;
		}
		
		Client client = clientBorrow();
		try{			
			for(DeletePack pack:DeletePack.getDeletePack(inPath)){
				client.remove(pack.getKsp(), 
				pack.getKey(), 
				pack.getCf(), 
				pack.getTimestamp(), 
				pack.getConsistencyLevel());				
			}
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

	public ResultAsListOfColumnOrSuperColumn getAllLinks4(CassandraVirtualPath inPath, SlicePredicate inPredicate) {
		// ===
		String ksp = inPath.getPathPart(PARTS.P1_KSP);
		ColumnParent cf = new ColumnParent(inPath._kspBean.getLinkTableName());
		String key = inPath.getPathPart(PARTS.P3_KEY);
		ConsistencyLevel cLevel = ConsistencyLevel.ONE;
		
		// init new result
		ResultAsListOfColumnOrSuperColumn result = new ResultAsListOfColumnOrSuperColumn();		
		
		// try to get records
		Cassandra.Client client = clientBorrow();
		try {
			result.setColumnOrSuperColumn(client.get_slice(ksp,	key, cf, inPredicate, cLevel));
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			getLog().error(e.toString());
			result.setResult(e.toString());
		}finally{
			clientRelease(client);
		}		
		
		// ===
		return result;
		
	}

}
