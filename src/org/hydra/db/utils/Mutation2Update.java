package org.hydra.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.abstracts.ALogger;

public class Mutation2Update extends ALogger{

	private static Log _log = 	LogFactory.getLog("org.hydra.db.utils.MutationPack");	
	
	/**
	 * @param inPath
	 * @param inBatchMap
	 * @return {@code Map<Key(String), Map<CfName(String), List<Mutation>>>}
	 * 
	 */
	public static Map<String, Map<String, List<Mutation>>> generate(
			CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inBatchMap) {
		
		_log.debug("Mutation pack: " + inPath.getPath());
		_log.debug(" ... mutation column count: " + inBatchMap.size());
		
		Map<String, Map<String, List<Mutation>>> resultMutationMap = 
			new HashMap<String, Map<String,List<Mutation>>>();
		
		if(inBatchMap.size() == 0){
			_log.warn("Nothing to mutate!");
			return resultMutationMap;
		}
		
		switch (inPath.getPathType()) {
		
		case KSP___CF:
		case KSP___CF___ID:
			mutations4KspCf(inPath, inBatchMap, resultMutationMap);
			break;

		case KSP___CF___ID___LINKNAME:
		case KSP___CF___ID___LINKNAME__LINKID:
			mutations4KspCfIDLinks(inPath, inBatchMap, resultMutationMap);
			break;
			
		default:
			_log.error(String.format("Unknow path(%s) and type(%s) to update!",
						inPath.getPath(), 
						inPath.getPathType()));
		}		

		if(_log.isDebugEnabled()){
			DBUtils.debugMutationResult(resultMutationMap);		
		}
		return resultMutationMap;
	}

	private static void mutations4KspCfIDLinks(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inMutaionMap,
			Map<String, Map<String, List<Mutation>>> inResultMap) {
		
		// setup access path for links
		CassandraVirtualPath linksVPath = new CassandraVirtualPath(
				inPath.getDescriptor(),
				String.format("%s" + CassandraVirtualPath.PATH_DELIMETER + "%s", inPath._kspBean.getName(),
						inPath._cfLinkBean.getName()));
		// create mutation
		List<byte[]> linkIDs = mutations4KspCf(linksVPath,
				inMutaionMap,
				inResultMap);

		// create mutation for links
		mutations4Links(inPath, linkIDs, inResultMap);		
		
	}

	private static void mutations4Links(CassandraVirtualPath inPath,
			List<byte[]> linkIDs,
			Map<String, Map<String, List<Mutation>>> inResultMap) {

		// generate list of columns
		List<Column> listOfColumns = new ArrayList<Column>();
		for (byte[] id : linkIDs) {
			listOfColumns.add(new Column(id,
								DBUtils.string2UTF8Bytes(Constants.GetUUID()),
								DBUtils.getCassandraTimestamp()));
		}

		// generate single list of mutation
		SuperColumn superColumn = new SuperColumn();
		superColumn.setName(DBUtils.string2UTF8Bytes(inPath._cfLinkBean.getName()));
		superColumn.setColumns(listOfColumns);

		ColumnOrSuperColumn columnOrSuperColumn = new ColumnOrSuperColumn();
		columnOrSuperColumn.setSuper_column(superColumn);

		Mutation mutation = new Mutation();
		mutation.setColumn_or_supercolumn(columnOrSuperColumn);
		
		List<Mutation> listOfMutation = new ArrayList<Mutation>();
		listOfMutation.add(mutation);

		DBUtils.joinMutationResults(
				inPath._kspBean.getLinkTableName(), 
				inPath.getID(), 
				listOfMutation, 
				inResultMap);
	}

	private static List<byte[]> mutations4KspCf(CassandraVirtualPath inPath,
			Map<byte[], Map<byte[], byte[]>> inMutaionMap,
			Map<String, Map<String, List<Mutation>>> inResultMap) {
		
		List<byte[]> result = new ArrayList<byte[]>();

		List<Mutation> listOfMutation =	new ArrayList<Mutation>();			
		
		// configure super columns to mutate
		for (Map.Entry<byte[], Map<byte[], byte[]>> mapIdColsVals : inMutaionMap.entrySet()) {

			// ... setup list of columns
			List<Column> listOfColumns = new ArrayList<Column>();
			for (Map.Entry<byte[], byte[]> mapColVal : mapIdColsVals.getValue()
					.entrySet()) {
				listOfColumns.add(new Column(mapColVal.getKey(), 
								mapColVal.getValue(),
								DBUtils.getCassandraTimestamp()));
			}
			// ... setup mutation for super column
			SuperColumn superColumn = new SuperColumn();

			superColumn.setName(mapIdColsVals.getKey());
			superColumn.setColumns(listOfColumns);

			ColumnOrSuperColumn columnOrSuperColumn = new ColumnOrSuperColumn();

			columnOrSuperColumn.setSuper_column(superColumn);

			Mutation mutation = new Mutation();
			mutation.setColumn_or_supercolumn(columnOrSuperColumn);

			listOfMutation.add(mutation);

			// ... add ID as result for further use
			result.add(mapIdColsVals.getKey());
		}
		
		DBUtils.joinMutationResults(
				inPath._cfBean.getName(), 
				Constants.KEY_COLUMNS_DEF, 
				listOfMutation, 
				inResultMap);		
		
		return result;
		
	}

}
