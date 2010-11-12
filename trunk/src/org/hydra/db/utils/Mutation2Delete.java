package org.hydra.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.Mutation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.db.ColumnFamilyBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.hydra.utils.abstracts.ALogger;

public class Mutation2Delete extends ALogger{

	private static Log _log = LogFactory.getLog(Mutation2Delete.class);	
	
	/**
	 * @param inPath
	 * @return {@code Map<Key(String), Map<CfName(String), List<Mutation>>>}
	 * 
	 */
	public static Map<String, Map<String, List<Mutation>>> generate(
			CassandraAccessorBean accessor,
			CassandraVirtualPath inPath) {
		
		_log.debug("Generate delete mutation pack for: " + inPath.getPath());
		_log.debug("Deletion type: " + inPath.getPathType());
		
		Map<String, Map<String, List<Mutation>>> result = 
			new HashMap<String, Map<String,List<Mutation>>>();
		
		switch (inPath.getPathType()) {
		case KSP___CF:
			mutations4KspCf(accessor, inPath, result, 0);
			break;
		case KSP___CF___KEY:
			mutations4KspCfKey(accessor, inPath, result, 0);
			break;

		case KSP___CF___KEY___SUPER:
			mutations4KspCfKeySuper(accessor, inPath, result, 0);
			break;
			
		case KSP___CF___KEY___SUPER__ID:
			mutations4KspCfKeySuperId(accessor, inPath, result, 0);
			break;			

		default:
			_log.error(String.format("Unknow path(%s) and type(%s) to update!",
						inPath.getPath(), 
						inPath.getPathType()));
		}		
		
		if(_log.isDebugEnabled())
			DBUtils.debugMutationResult(result);		

		return result;
	}
	
	private static void mutations4KspCf(
			CassandraAccessorBean accessor,
			CassandraVirtualPath inPath,
			Map<String, Map<String, List<Mutation>>> inResultMap, 
			int inRecursionDeep)  {
		
		// ... test path
		if(inPath.getPathType() != PATH_TYPE.KSP___CF){
			_log.error("Path type should be: KSP___CF");
			return;
		}		
		
		// ... setup prefix
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";		

		// ... debug
		_log.debug(recursionDeepPrefixStr + "Make deletion pack for: " + inPath.getPath());				
		
		// ... main process
		ResultAsListOfColumnOrSuperColumn result = accessor.find(inPath);
		if(!result.isOk()){
			_log.error("mutations4KspCf: " + result.getResult());
			return;
		}
		
		// ... iterate over childs
		for(ColumnOrSuperColumn columnOrSuperColumn:result.getColumnOrSuperColumn()){
			
			if(!columnOrSuperColumn.isSetSuper_column()){
				_log.error("mutations4KspCf: result sould be super column!");
				continue;			
			}
			
			String superColumnID = DBUtils.bytes2UTF8String(columnOrSuperColumn.super_column.name);
			_log.error("mutations4KspCf: DELETE for super column: " + superColumnID);
			
			CassandraVirtualPath childPath = new CassandraVirtualPath(inPath.getDescriptor(),
					inPath._kspBean.getName() + // ksp
					CassandraVirtualPath.PATH_DELIMETER +
					inPath._cfBean.getName() +  // cf
					CassandraVirtualPath.PATH_DELIMETER +
					superColumnID);             // key			
			
			mutations4KspCfKey(accessor, childPath, inResultMap, inRecursionDeep + 1);			
		}		
	}

	private static void mutations4KspCfKey(
			CassandraAccessorBean accessor,
			CassandraVirtualPath inPath,
			Map<String, Map<String, List<Mutation>>> inResultMap, int inRecursionDeep) {
		
		// ... setup prefix
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";
		
		// ... debug
		_log.debug(recursionDeepPrefixStr + "Make deletion pack for: " + inPath.getPath());		
				
		// ... setup list of mutations
		List<Mutation> listOfMutations = new ArrayList<Mutation>();
		listOfMutations.add(getDeleteMutation4SuperColumn(inPath.getKey(), null));
		
		// ... delete self
		DBUtils.joinMutationResults(
				inPath._cfBean.getName(), 
				Constants.KEY_COLUMNS_DEF, 
				listOfMutations, inResultMap);
		
		// ... delete childs
		if(inPath._cfBean.hasChilds()){
			for(ColumnFamilyBean child:inPath._cfBean.getChilds()){
				CassandraVirtualPath childPath = new CassandraVirtualPath(inPath.getDescriptor(),
						inPath._kspBean.getName() + // ksp
						CassandraVirtualPath.PATH_DELIMETER +
						inPath._cfBean.getName() +  // cf
						CassandraVirtualPath.PATH_DELIMETER +
						inPath.getKey() +            // key
						CassandraVirtualPath.PATH_DELIMETER +
						child.getName());           // super				

				mutations4KspCfKeySuper(accessor, childPath, inResultMap, inRecursionDeep + 1);
			}
		}
	}

	private static void mutations4KspCfKeySuper(
			CassandraAccessorBean accessor,
			CassandraVirtualPath inPath,
			Map<String, Map<String, 
			List<Mutation>>> inResultMap, 
			int inRecursionDeep) {
		// ... test path
		if(inPath.getPathType() != PATH_TYPE.KSP___CF___KEY___SUPER){
			_log.error("Path type should be: PATH_TYPE.KSP___CF___ID___LINKNAME");
			return;
		}		
		
		// ... setup prefix
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";		

		// ... debug
		_log.debug(recursionDeepPrefixStr + "Make deletion pack for: " + inPath.getPath());			
		
		// ... process over links
		ResultAsListOfColumnOrSuperColumn result = accessor.find(inPath);
		
		if(result.isOk() 
		&& result.getColumnOrSuperColumn() != null 
		&& result.getColumnOrSuperColumn().size() != 0){
			
			for(ColumnOrSuperColumn cos:result.getColumnOrSuperColumn()){
				// ... LINKS
				List<Mutation> listOfMutations = new ArrayList<Mutation>();
				listOfMutations.add(getDeleteMutation4SuperColumn(cos.super_column.name, null));
				
				DBUtils.joinMutationResults(
						inPath._kspBean.getLinkTableName(), 
						inPath.getKey(), 
						listOfMutations, inResultMap);
				
				// ... CHILDS
				for(Column col: cos.super_column.columns){
					CassandraVirtualPath childPath = new CassandraVirtualPath(inPath.getDescriptor(),
							inPath._kspBean.getName() + // ksp
							CassandraVirtualPath.PATH_DELIMETER +
							inPath._cfBean.getName() +  // cf
							CassandraVirtualPath.PATH_DELIMETER +
							inPath.getKey() +            // key
							CassandraVirtualPath.PATH_DELIMETER +
							inPath.getSuper() +      // super
							CassandraVirtualPath.PATH_DELIMETER +
							DBUtils.bytes2UTF8String(col.name)); // col
							
					mutations4KspCfKeySuperId(accessor, childPath, inResultMap, inRecursionDeep + 1);
				}
			}
		}else{
			_log.debug(recursionDeepPrefixStr + " ... but no " + inPath.getSuper());					
		}
	}
			
	private static void mutations4KspCfKeySuperId(
			CassandraAccessorBean accessor,
			CassandraVirtualPath inPath,
			Map<String, Map<String, List<Mutation>>> inResultMap, 
			int inRecursionDeep) {
		// ... test path
		if(inPath.getPathType() != PATH_TYPE.KSP___CF___KEY___SUPER__ID){
			_log.error("Path type should be: PATH_TYPE.KSP___CF___ID___LINKNAME__LINKID");
			return;
		}		
				
		// ... setup prefix
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";
		
		// ... remove for links
		if(inRecursionDeep == 0){
			/**
			 * inRecursionDeep != 0 means that parent mutation already
			 * removed links records
			 */
			List<Mutation> listOfMutations = new ArrayList<Mutation>();
			listOfMutations.add(
					getDeleteMutation4SuperColumn(
							inPath.getPathPart(PARTS.P4_SUPER),
							inPath.getPathPart(PARTS.P5_COL)
						)
				);
			
			DBUtils.joinMutationResults(
					inPath._kspBean.getLinkTableName(), 
					inPath.getKey(), 
					listOfMutations, inResultMap);
		}
				
		// ... CHILDS
		CassandraVirtualPath childPath = new CassandraVirtualPath(inPath.getDescriptor(),
				inPath._kspBean.getName() +           // ksp
				CassandraVirtualPath.PATH_DELIMETER +
				inPath.getPathPart(PARTS.P4_SUPER) +  // cf
				CassandraVirtualPath.PATH_DELIMETER +
				inPath.getPathPart(PARTS.P5_COL));    // id					
			
			mutations4KspCfKey(accessor, childPath, inResultMap, inRecursionDeep + 1);		
	}

	// ### UTILS
	private static Mutation getDeleteMutation4SuperColumn(
			String inSuper, 
			String inCol){
		return getDeleteMutation4SuperColumn(DBUtils.string2UTF8Bytes(inSuper), DBUtils.string2UTF8Bytes(inCol));
	}
		
	private static Mutation getDeleteMutation4SuperColumn(byte[] inSuper, byte[] inCol) {		
		Deletion deletion = new Deletion(DBUtils.getCassandraTimestamp());
		deletion.setSuper_column(inSuper);
		
		if(inCol != null) deletion.setPredicate(DBUtils.getSlicePredicate4Col(inCol));
		
		Mutation mutation = new Mutation();
		mutation.setDeletion(deletion);		
		
		return mutation;
	}	

	
}
