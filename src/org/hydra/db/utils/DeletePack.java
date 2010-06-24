package org.hydra.db.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.PARTS;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.hydra.utils.abstracts.ALogger;

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
			result.add(delete4KspCf(inPath));			
			break;
		case KSP___CF___ID:
			DeletePack pack = delete4KspCf(inPath);
			pack.getCf().setSuper_column(DBUtils.string2UTF8Bytes(inPath.getPathPart(PARTS.P3_KEY)));
			result.add(pack);
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

	private static void delete4KspCfIDLinksLinkID(CassandraVirtualPath inPath,
			List<DeletePack> result) {
		// Part #1 delete real DB objects
		// ... get all linked IDs
		CassandraAccessorBean accessor = Utils4Tests.getAccessor();
		ResultAsListOfColumnOrSuperColumn dbLinks = accessor.find(inPath);
		
		// ... for cycle
		if(dbLinks.isOk()){
			for(Column col: dbLinks.getColumnOrSuperColumn().get(0).super_column.columns){
				DeletePack pack = new DeletePack();
				
				pack.setKsp(inPath.getPathPart(PARTS.P1_KSP));
				pack.setCf(new ColumnPath(inPath.getPathPart(PARTS.P4_SUPER)));
				pack.getCf().setSuper_column(col.name);
				pack.setKey(KEY_COLUMNS_DEF);
				pack.setTimestamp(System.currentTimeMillis());
				pack.setConsistencyLevel(ConsistencyLevel.ONE);
				
				result.add(pack);
			}
		}		
	}

	private static void delete4KspCfIDLinks(CassandraVirtualPath inPath, List<DeletePack> result) {
		// Part #1 delete real DB objects
		// ... get all linked IDs
		CassandraAccessorBean accessor = Utils4Tests.getAccessor();
		ResultAsListOfColumnOrSuperColumn dbLinks = accessor.find(inPath);
		
		// ... for cycle
		if(dbLinks.isOk()){
			for(Column col: dbLinks.getColumnOrSuperColumn().get(0).super_column.columns){
				DeletePack pack = new DeletePack();
				
				pack.setKsp(inPath.getPathPart(PARTS.P1_KSP));
				pack.setCf(new ColumnPath(inPath.getPathPart(PARTS.P4_SUPER)));
				pack.setKey(DBUtils.bytes2UTF8String(col.name));
				pack.setTimestamp(System.currentTimeMillis());
				pack.setConsistencyLevel(ConsistencyLevel.ONE);
				
				result.add(pack);
			}
		}
		// Part #2 delete records from links table
	}

	private static DeletePack delete4KspCf(CassandraVirtualPath inPath) {
		DeletePack pack = new DeletePack();
		
		pack.setKsp(inPath.getPathPart(PARTS.P1_KSP));
		pack.setCf(new ColumnPath(inPath.getPathPart(PARTS.P2_CF)));
		pack.setKey(KEY_COLUMNS_DEF);
		pack.setTimestamp(System.currentTimeMillis());
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
