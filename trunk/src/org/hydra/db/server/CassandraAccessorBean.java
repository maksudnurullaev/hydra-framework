package org.hydra.db.server;

import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.db.utils.Mutation2Delete;
import org.hydra.db.utils.Mutation2Update;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;

public class CassandraAccessorBean extends ACassandraAccessor {
	public ResultAsListOfColumnOrSuperColumn find(CassandraVirtualPath inPath) {

		getLog().debug("Try to get data for path: " + inPath.getPath());

		ResultAsListOfColumnOrSuperColumn result =
				new ResultAsListOfColumnOrSuperColumn();

		Result tempResult = DBUtils.test4NullKspCf(inPath);

		if (!tempResult.isOk()) {
			result.setResult(false);
			result.setResult(tempResult.getResult());
			return result;
		}

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
			predicate = new SlicePredicate();
			cLevel = ConsistencyLevel.ONE;
			break;

		case KSP___CF___ID:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
			key = KEY_COLUMNS_DEF;
			predicate = DBUtils.getSlicePredicateStr(inPath.getPathPart(PARTS.P3_KEY));
			cLevel = ConsistencyLevel.ONE;
			break;

		case KSP___CF___ID___LINKNAME:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath._kspBean.getLinkTableName());
			key = inPath.getPathPart(PARTS.P3_KEY);
			predicate = DBUtils.getSlicePredicateStr(inPath.getPathPart(PARTS.P4_SUPER));
			cLevel = ConsistencyLevel.ONE;
			break;

		case KSP___CF___ID___LINKNAME__LINKID:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath.getPathPart(PARTS.P4_SUPER));
			key = KEY_COLUMNS_DEF;			
			predicate = DBUtils.getSlicePredicateStr(inPath.getPathPart(PARTS.P5_COL));
			cLevel = ConsistencyLevel.ONE;			
			break;
		default:
			String errStr = String.format("Unknow path(%s) to get db records!",
					inPath.getPath());

			result.setResult(false);
			result.setResult(errStr);
			getLog().error(errStr);
			return result;
		}

		Cassandra.Client client = clientBorrow();

		try {
			result.setColumnOrSuperColumn(
					client.get_slice(ksp, key, cf, predicate, cLevel));
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			getLog().error(e.toString());
			result.setResult(e.toString());
		} finally {
			clientRelease(client);
		}
		return result;
	}

	public Result update(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inBatchMap) {
		Result result = new Result();

		if (inBatchMap == null || inBatchMap.size() == 0) {
			result.setResult(false);
			result.setObject("Invalid batch map!");
			getLog().warn("Invalid batch map!");
			return result;
		}

		Client client = clientBorrow();
		try {
			client.batch_mutate(
					inPath._kspBean.getName(), 
					Mutation2Update.generate(inPath, inBatchMap),
					ConsistencyLevel.ONE);
			
			result.setResult(true);
			result.setResult("Batch mutate accepted!");
			getLog().debug("Batch mutate accepted!");
			
		} catch (Exception e) {
			result.setResult(false);
			result.setResult(e.getMessage());
			getLog().error(e.getMessage());
		} finally {
			clientRelease(client);
		}
		
		return result;
	}

	public Result delete(CassandraVirtualPath inPath) {
		// tests path
		Result result = DBUtils.test4NullKspCf(inPath);
		if (!result.isOk()) {
			getLog().error(result.getResult());
			return result;
		}
		
		Client client = clientBorrow();
		try {
			Map<String, Map<String, List<Mutation>>> deletions = 
				Mutation2Delete.generate(inPath);
//			for(Map.Entry<String, Map<String, List<Mutation>>> mapKeyMapCfMutations: deletions.entrySet()){
//				for(Map.Entry<String, List<Mutation>> mapCfMutations: mapKeyMapCfMutations.getValue().entrySet()){
//					List<Mutation> listOfMutaions = mapCfMutations.getValue();
//					for(Mutation mutation:listOfMutaions){
//						if(mutation.isSetDeletion() && 
//								mutation.getDeletion().isSetSuper_column() &&
//								mutation.getDeletion().isSetPredicate() && 
//								mutation.getDeletion().getPredicate().isSetSlice_range()
//								)
//						{
//							delete4version6without_mutation(
//									inPath._kspBean.getName(), // Ksp 
//									mapCfMutations.getKey(),
//									mapKeyMapCfMutations.getKey(), 
//									mutation.getDeletion().super_column, 
//									mutation.getDeletion().getPredicate().slice_range.getStart());
//							// ... remove db
//							// ... remove from list of mutation
//							listOfMutaions.remove(mutation);
//						}
//					}
//				}
//			}
				
			client.batch_mutate(
					inPath._kspBean.getName(), 
					Mutation2Delete.generate(inPath),
					ConsistencyLevel.ONE);
			
			result.setResult(true);
			result.setResult("Batch mutate accepted!");
			getLog().debug("Batch mutate accepted!");
			
		} catch (Exception e) {
			result.setResult(false);
			result.setResult(e.getMessage());
			getLog().error(e.getMessage());
		} finally {
			clientRelease(client);
		}
		
		return result;		
	}
	
	/**
	 * We use it because have a deletion problem for Mutation+Deletion+Predicate
	 * and have error: "Deletion does not yet support SliceRange predicates."
	 * <blockquote><strong>Note:</strong> We hope that will be fixed at nearest versions of Cassnadra!!!</blockquote>
	 * @param inKsp - mandatory
	 * @param inCf - madatory
	 * @param inKey - mandatory
	 * @param inSuper - optional
	 * @param inCol - optional
	 * @return Result
	 */
	public Result delete4version6without_mutation(String inKsp, String inCf, String inKey, String inSuper, String inCol){
		Result result = new Result();
		
		Client client = DBUtils.getAccessor().clientBorrow();		
		ColumnPath cfPath = new ColumnPath(inCf);
		
		if(inSuper != null)cfPath.setSuper_column(DBUtils.string2UTF8Bytes(inSuper));
		if(inCol != null) cfPath.setColumn(DBUtils.string2UTF8Bytes(inCol));
		
		try {
			client.remove(inKsp, inKey, cfPath , DBUtils.getCassandraTimestamp(), ConsistencyLevel.ONE);			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			DBUtils.getAccessor().clientRelease(client);
		}
		
		return result;		
	}
	
	//TODO [test and if not used, remove it later]	
	public ResultAsListOfColumnOrSuperColumn getLinks4(
			CassandraVirtualPath inPath, String inLinkName) {
		
		String ksp = inPath.getPathPart(PARTS.P1_KSP);
		ColumnParent cf = new ColumnParent(inPath._kspBean.getLinkTableName());
		String key = inPath.getPathPart(PARTS.P3_KEY);
		ConsistencyLevel cLevel = ConsistencyLevel.ONE;

		ResultAsListOfColumnOrSuperColumn result = new ResultAsListOfColumnOrSuperColumn();

		Cassandra.Client client = clientBorrow();

		try {
			result.setColumnOrSuperColumn(
					client.get_slice(
							ksp, 
							key, 
							cf, 
							DBUtils.getSlicePredicateStr(inLinkName), 
							cLevel));
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			getLog().error(e.toString());
			result.setResult(e.toString());
		} finally {
			clientRelease(client);
		}

		return result;

	}

}
