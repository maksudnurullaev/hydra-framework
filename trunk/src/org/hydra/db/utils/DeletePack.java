package org.hydra.db.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.hydra.utils.abstracts.ALogger;
import org.junit.Assert;

public class DeletePack extends ALogger {
	private static Log _log = 	LogFactory.getLog("org.hydra.db.utils.DeletePack");	
	public static final String KEY_COLUMNS_DEF = "COLUMNS";
	
	private String ksp = null;
	private String key = null;
	private ColumnPath cf = null;
	private long timestamp = 0;
	private ConsistencyLevel consistencyLevel = null;

	public void setKsp(String ksp) {
		this.ksp = ksp;
	}

	public String getKsp() {
		return ksp;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setCf(ColumnPath cf) {
		this.cf = cf;
	}

	public ColumnPath getCf() {
		return cf;
	}
	
	public static final List<DeletePack> getDeletePack(CassandraVirtualPath inPath){
		List<DeletePack> result = new ArrayList<DeletePack>();
		
		switch(inPath.getPathType()){
		case KSP___CF:
			result.add(delete4KspCfXXXEvil(inPath));			
			break;
		case KSP___CF___ID:
			deleteKspCfIdXXX(inPath, result, 0);
			break;
		case KSP___CF___ID___LINKNAME:
			delete4KspCfIDLinks(inPath, result);
			break;
		case KSP___CF___ID___LINKNAME__LINKID:
			delete4KspCfIDLinksLinkID(inPath, result);
			break;
		default:
				_log.error("Unknown path: " + inPath.getPath());
				result.clear();
		}
		return result;
	}

	private static void deleteKspCfIdXXX(CassandraVirtualPath inPath,
			List<DeletePack> inResult, int inRecursionDeep) {
		
		String recursionDeepPrefixStr = "";
		for (int i = 0; i < inRecursionDeep; i++) recursionDeepPrefixStr += " ... ";
		
		_log.debug(recursionDeepPrefixStr + "Make deletion pack for: " + inPath.getPath());
		// delete Ksp.Cf.ID
		DeletePack pack = delete4KspCfXXXEvil(inPath);
		pack.getCf().setSuper_column(DBUtils.string2UTF8Bytes(inPath.getPathPart(PARTS.P3_KEY)));		
		inResult.add(pack);

		// delete Ksp.Cf.ID.CHILDs
		Set<ColumnFamilyBean> childs = inPath._cfBean.getChilds();
		if(childs != null){
			// ... ... call deleteKspCfIdXXX for each child  
			for(ColumnFamilyBean childColumnFamilyBean: childs){
				_log.debug(recursionDeepPrefixStr + " ... found childs: " + childColumnFamilyBean.getName());
				// get all Ksp.Cf.ID.[cfb.getName()].chilIDs
				ResultAsListOfColumnOrSuperColumn result = Utils4Tests.getAccessor().getAllLinks4(inPath, 
						DBUtils.getSlicePredicate(childColumnFamilyBean.getName(), childColumnFamilyBean.getName()));
				
				if(result.isOk() 
						&& result.getColumnOrSuperColumn() != null 
						&& result.getColumnOrSuperColumn().size() != 0){
					
					Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
					ColumnOrSuperColumn columnOrSuperColumn = listIterator.next();
					
					for(Column column:columnOrSuperColumn.getSuper_column().columns){
						// ... ... deletioin for child
						String childPathStr = String.format("%s.%s.%s", 
								inPath._kspBean.getName(),
								childColumnFamilyBean.getName(),
								DBUtils.bytes2UTF8String(column.name));
						// _log.debug(recursionPrefixStr + "Delete child: "+ childPathStr);
						CassandraVirtualPath childPath = new CassandraVirtualPath(inPath.getDescriptor(),
								childPathStr);
						// ... ... recursive delete again
						deleteKspCfIdXXX(childPath, inResult, inRecursionDeep + 1);
					}
				}else{
					_log.debug(recursionDeepPrefixStr + " ... but no " + childColumnFamilyBean.getName());					
				}
			}
		}
		
		if(inRecursionDeep == 0)
			_log.debug(recursionDeepPrefixStr + "Found " + inResult.size() + " pack(s) for deletion!");			
	}

	//TODO Fix It
	private static void delete4KspCfIDLinksLinkID(CassandraVirtualPath inPath,
			List<DeletePack> result) {
//		// Part #1 delete real DB objects
//		// ... get all linked IDs
//		CassandraAccessorBean accessor = Utils4Tests.getAccessor();
//		ResultAsListOfColumnOrSuperColumn dbLinks = accessor.find(inPath);
//		
//		// ... for cycle
//		if(dbLinks.isOk()){
//			for(Column col: dbLinks.getColumnOrSuperColumn().get(0).super_column.columns){
//				DeletePack pack = new DeletePack();
//				
//				pack.setKsp(inPath.getPathPart(PARTS.P1_KSP));
//				pack.setCf(new ColumnPath(inPath.getPathPart(PARTS.P4_SUPER)));
//				pack.getCf().setSuper_column(col.name);
//				pack.setKey(KEY_COLUMNS_DEF);
//				pack.setTimestamp(DBUtils.getCassandraTimestamp());
//				pack.setConsistencyLevel(ConsistencyLevel.ONE);
//				
//				result.add(pack);
//			}
//		}		
	}

	private static void delete4KspCfIDLinks(CassandraVirtualPath inPath, List<DeletePack> result) {
		// Part #1 delete real DB objects

		CassandraAccessorBean accessor = Utils4Tests.getAccessor();
		ResultAsListOfColumnOrSuperColumn dbLinks = accessor.find(inPath);
		
		// ... for cycle
		if(dbLinks.isOk()){
			
			_log.debug("Delete column count: " 
					+ dbLinks.getColumnOrSuperColumn().get(0).super_column.columns.size());
			
			for(Column col: dbLinks.getColumnOrSuperColumn().get(0).super_column.columns){
				// ... delete db objects
				DeletePack delPack = new DeletePack();
				// ... ... ksp
				delPack.setKsp(inPath.getPathPart(PARTS.P1_KSP));
				// ... ... cf
				delPack.setCf(new ColumnPath(inPath.getPathPart(PARTS.P4_SUPER)));
				// ... ... super
				delPack.getCf().setSuper_column(col.name);
				// ... ... key
				delPack.setKey(KEY_COLUMNS_DEF);
				// ... ... timestamp & consistency
				delPack.setTimestamp(DBUtils.getCassandraTimestamp());
				delPack.setConsistencyLevel(ConsistencyLevel.ONE);
				// ... pack
				result.add(delPack);				
			}
		}
		// Part #2 delete records from links table
		// ... delete db objects links
		DeletePack delDBLink = new DeletePack();
		// ... ... ksp
		delDBLink.setKsp(inPath.getPathPart(PARTS.P1_KSP));
		// ... ... cf
		delDBLink.setCf(new ColumnPath(inPath._kspBean.getLinkTableName()));
		// ... ... super
		delDBLink.getCf().setSuper_column(DBUtils.string2UTF8Bytes(inPath.getPathPart(PARTS.P4_SUPER)));
		// ... ... key
		delDBLink.setKey(inPath.getPathPart(PARTS.P3_KEY));
		// ... ... timestamp & consistency		
		delDBLink.setTimestamp(DBUtils.getCassandraTimestamp());
		delDBLink.setConsistencyLevel(ConsistencyLevel.ONE);
		// ... pack
		result.add(delDBLink);
	}

	private static DeletePack delete4KspCfXXXEvil(CassandraVirtualPath inPath) {
		DeletePack pack = new DeletePack();
		
		pack.setKsp(inPath.getPathPart(PARTS.P1_KSP));
		pack.setCf(new ColumnPath(inPath.getPathPart(PARTS.P2_CF)));
		pack.setKey(KEY_COLUMNS_DEF);
		pack.setTimestamp(DBUtils.getCassandraTimestamp());
		pack.setConsistencyLevel(ConsistencyLevel.ONE);
		
		return pack;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
		this.consistencyLevel = consistencyLevel;
	}

	public ConsistencyLevel getConsistencyLevel() {
		return consistencyLevel;
	}	
}
