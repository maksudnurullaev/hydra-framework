package org.hydra.db.server;

import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;
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
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			predicate.setSlice_range(sliceRange);
			
			cLevel = ConsistencyLevel.ONE;
			break;

		case KSP___CF___KEY:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath.getPathPart(PARTS.P2_CF));
			key = KEY_COLUMNS_DEF;
			predicate = DBUtils.getSlicePredicateStr(inPath.getPathPart(PARTS.P3_KEY));
			cLevel = ConsistencyLevel.ONE;
			break;

		case KSP___CF___KEY___SUPER:
			ksp = inPath.getPathPart(PARTS.P1_KSP);
			cf = new ColumnParent(inPath._kspBean.getLinkTableName());
			key = inPath.getPathPart(PARTS.P3_KEY);
			predicate = DBUtils.getSlicePredicateStr(inPath.getPathPart(PARTS.P4_SUPER));
			cLevel = ConsistencyLevel.ONE;
			break;

		case KSP___CF___KEY___SUPER__ID:
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
			client.batch_mutate(
					inPath._kspBean.getName(), 
					Mutation2Delete.generate(this, inPath),
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
	
	
}
