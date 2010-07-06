package org.hydra.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.Mutation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.PATH_TYPE;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.hydra.utils.abstracts.ALogger;

public class Mutation2Delete extends ALogger{

	private static Log _log = 	LogFactory.getLog("org.hydra.db.utils.MutationPack");	
	
	/**
	 * @param inPath
	 * @return {@code Map<Key(String), Map<CfName(String), List<Mutation>>>}
	 * 
	 */
	public static Map<String, Map<String, List<Mutation>>> generate(
			CassandraVirtualPath inPath) {
		
		_log.debug("Generate delete mutation pack for: " + inPath.getPath());
		
		Map<String, Map<String, List<Mutation>>> result = 
			new HashMap<String, Map<String,List<Mutation>>>();
		
		switch (inPath.getPathType()) {
		case KSP___CF:
			mutations4KspCf(inPath, result, 0);
			break;
		case KSP___CF___ID:
			mutations4KspCfId(inPath, result, 0);
			break;

		case KSP___CF___ID___LINKNAME:
			//TODO mutations4KspCfIDLinks(inPath, result);
			_log.error("[[[mutations4KspCfIDLinks(inPath, result)]]] it's not implemented yet!");
			break;
			
		case KSP___CF___ID___LINKNAME__LINKID:
			//TODO mutations4KspCfIDLinksID(inPath, result);
			_log.error("[[[mutations4KspCfIDLinksID(inPath, result)]]] it's not implemented yet!");
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
	
	private static void mutations4KspCf(CassandraVirtualPath inPath,
			Map<String, Map<String, List<Mutation>>> inResultMap, int inRecursionDeep)  {
		
		CassandraAccessorBean accessor = BeansUtils.getAccessor();
		
		ResultAsListOfColumnOrSuperColumn result = accessor.find(inPath);
		if(!result.isOk()){
			_log.error("mutations4KspCf: " + result.getResult());
			return;
		}
		
		for(ColumnOrSuperColumn columnOrSuperColumn:result.getColumnOrSuperColumn()){
			
			if(!columnOrSuperColumn.isSetSuper_column()){
				_log.error("mutations4KspCf: result sould be super column!");
				continue;			
			}
			
			String superColumnID = DBUtils.bytes2UTF8String(columnOrSuperColumn.super_column.name);
			_log.error("mutations4KspCf: DELETE for super column: " + superColumnID);
			
			CassandraVirtualPath path = new CassandraVirtualPath(BeansUtils.getDescriptor(),
					inPath.getPath() + CassandraVirtualPath.PATH_DELIMETER + superColumnID);
			
			mutations4KspCfId(path, inResultMap, inRecursionDeep + 1);			
		}		
	}

	private static void mutations4KspCfId(CassandraVirtualPath inPath,
			Map<String, Map<String, List<Mutation>>> inResultMap, int inRecursionDeep) {
		
		// ... setup prefix
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";
		
		// ... debug
		_log.debug(recursionDeepPrefixStr + "Make deletion pack for: " + inPath.getPath());		
				
		// ... setup list of mutations
		List<Mutation> listOfMutations = new ArrayList<Mutation>();
		listOfMutations.add(getDeleteMutation4SuperColumn(inPath.getID()));
		
		// ... setup result
		DBUtils.joinMutationResults(
				inPath._cfBean.getName(), 
				Constants.KEY_COLUMNS_DEF, 
				listOfMutations, inResultMap);
		
		// ... delete childs if exists
		mutations4ChildsIfExists4KfpCfID(inPath, inResultMap, inRecursionDeep + 1);
	}

	private static void mutations4ChildsIfExists4KfpCfID(CassandraVirtualPath inPath,
			Map<String, Map<String, List<Mutation>>> inResultMap, int inRecursionDeep) {	
		
		// ... test path
		if(inPath.getPathType() != PATH_TYPE.KSP___CF___ID){
			_log.error("Path type should be: PATH_TYPE.KSP___CF___ID");
			return;
		}		
				
		// ... setup prefix
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";
		
		// ... process over all childs
		if(inPath._cfBean.getChilds() != null){
			for(ColumnFamilyBean childColumnFamilyBean: inPath._cfBean.getChilds()){
				_log.debug(recursionDeepPrefixStr + " ... found childs: " + childColumnFamilyBean.getName());
				
				ResultAsListOfColumnOrSuperColumn result = DBUtils.getAccessor().getAllLinks4(inPath, 
						DBUtils.getSlicePredicate(childColumnFamilyBean.getName(), childColumnFamilyBean.getName()));
				
				if(result.isOk() 
						&& result.getColumnOrSuperColumn() != null 
						&& result.getColumnOrSuperColumn().size() != 0){
					
					Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
					while(listIterator.hasNext()){
						ColumnOrSuperColumn columnOrSuperColumn = listIterator.next();
						if(columnOrSuperColumn.isSetSuper_column()){
							
							// ... setup result for LINKS
							List<Mutation> listOfMutations = new ArrayList<Mutation>();
							listOfMutations.add(getDeleteMutation4SuperColumn(columnOrSuperColumn.super_column.name));
							
							DBUtils.joinMutationResults(
									inPath._kspBean.getLinkTableName(), 
									inPath.getID(), 
									listOfMutations, inResultMap);							
							
							for(Column column:columnOrSuperColumn.getSuper_column().columns){
								// ... ... deletioin for child
								String childPathStr = String.format("%s" + CassandraVirtualPath.PATH_DELIMETER 
										+ "%s" + CassandraVirtualPath.PATH_DELIMETER + "%s", 
										inPath._kspBean.getName(),
										childColumnFamilyBean.getName(),
										DBUtils.bytes2UTF8String(column.name));
								
								CassandraVirtualPath childPath = 
									new CassandraVirtualPath(inPath.getDescriptor(), childPathStr);
								
								mutations4KspCfId(childPath, inResultMap, inRecursionDeep + 1);
							}
							
						}else{
							_log.error(recursionDeepPrefixStr + " ... no proper super column found for: " + childColumnFamilyBean.getName());												
						}
					}
					
				}else{
					_log.debug(recursionDeepPrefixStr + " ... but no " + childColumnFamilyBean.getName());					
				}
			}
		}		
	}
	
	// ### UTILS
	private static Mutation getDeleteMutation4SuperColumn(String inKey){
		return getDeleteMutation4SuperColumn(DBUtils.string2UTF8Bytes(inKey));
	}
	
	private static Mutation getDeleteMutation4SuperColumn(byte[] inKeyAsBytes) {		
		Deletion deletion = new Deletion(DBUtils.getCassandraTimestamp());
		deletion.setSuper_column(inKeyAsBytes);
		
		Mutation mutation = new Mutation();
		mutation.setDeletion(deletion);		
		
		return mutation;
	}

	
}
