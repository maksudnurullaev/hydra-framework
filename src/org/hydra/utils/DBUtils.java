package org.hydra.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.junit.Assert;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class DBUtils {
	public static final String PATH2COLUMN5 = "%s.%s['%s']['%s']['%s']";
	public static final String PATH2COLUMN4   = "%s.%s['%s']['%s']";		
	
	private static Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");
	
	// **** defaults
	public static final String _action_cs_get_all_columns_id    = "cs_get_all_columns_id"; 	
	public static final String _utf8_encoding = "UTF8";
	
	// **** static functions
	
	public static String bytes2UTF8String(byte[] inBytes){
		return bytes2UTF8String(inBytes, 0);
	}	
	
	public static String bytes2UTF8String(byte[] inBytes, int trancateLength){
		String result = null;
		try {
			result = new String(inBytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(trancateLength > 0 && result.length() > trancateLength)
			return result.substring(0, trancateLength) + "...";
		
		return result;
	}
	
	public static byte[] string2UTF8Bytes(String inString){
		if(inString == null) return null;
		
		byte[] result = null;
		try {
			result = inString.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<byte[], Map<byte[], byte[]>> convert2Bytes(
			Map<String, Map<String, String>> inResult) {
		Map<byte[], Map<byte[], byte[]>> result = new HashMap<byte[], Map<byte[],byte[]>>();
		for(Entry<String, Map<String, String>> mapEntry: inResult.entrySet()){
			Map<byte[], byte[]> colNameMap = new HashMap<byte[], byte[]>();
			for(Entry<String, String> mapEntry2: mapEntry.getValue().entrySet()){
				colNameMap.put(string2UTF8Bytes(mapEntry2.getKey()), string2UTF8Bytes(mapEntry2.getValue()));
			}
			result.put(string2UTF8Bytes(mapEntry.getKey()), colNameMap);
		}
		return result;
	}

	public static Map<String, byte[]> converMapStringByteA(List<Column> columns) {
		Map<String, byte[]> result = new HashMap<String, byte[]>();
		for(Column column:columns){
			result.put(bytes2UTF8String(column.name), column.value);
		}
		return result;
	}	

	public static boolean validateFields(ColumnFamilyBean cf,
			Map<String, ?> inMap) {
		if(cf == null || inMap == null){
			_log.error("Invalid Cf or Map: NULL!");
			return false;
		}
		for(String key:inMap.keySet()){
			if(!cf.columns.containsKey(key)){
				_log.error(String.format("Could not find column(%s) for cf(%s)", key, cf.getName()));
				return false;
			}
		}
		return true;
	}

	public static Result test4NullKspCf(CassandraVirtualPath inPath){
		Result result = new Result();
		// validate path
		if(inPath == null 
				|| inPath._kspBean == null
				|| inPath._cfBean == null
				){
			String errStr = "Invalid access path!";
			_log.error(errStr);
			result.setResult(false);
			result.setResult(errStr);
			return result;
		}		
		result.setResult(true);
		return result;
	}	
	
	public static SlicePredicate getSlicePredicateStr(String inSliceRange){	
		return getSlicePredicate4Col(string2UTF8Bytes(inSliceRange));
	}
	
	public static SlicePredicate getSlicePredicate4Col(byte[] inCol){
	    List<byte[]> columns = new ArrayList<byte[]>();
	    columns.add(inCol);
	    
	    return getSlicePredicate4Col(columns);
	}
	
	public static SlicePredicate getSlicePredicate4SliceRange(byte[] inCol){
		SlicePredicate resultPredicate = new SlicePredicate();
		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(inCol);
		sliceRange.setFinish(inCol);
		resultPredicate.setSlice_range(sliceRange);
	    
	    return resultPredicate;
	}	
	
	public static SlicePredicate getSlicePredicate4Col(List<byte[]> inColNames){
		
        SlicePredicate predicate = new SlicePredicate();
        predicate.setColumn_names(inColNames);
		
		return predicate;		
	}		
	
	public static void describeColumn(CassandraDescriptorBean descriptor, IMessage inMessage){
		String keyspaceName = inMessage.getData().get(IMessage._data_cs_ksp);
		String columnFamilyName = inMessage.getData().get(IMessage._data_cs_cf); 
		String columnName = inMessage.getData().get(IMessage._data_cs_col); 
		
		String pathStr = String.format("%s--->%s", keyspaceName, columnFamilyName);
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, pathStr);
		
		if(!path.isValid()){
			inMessage.setError("Path to DB objects is valid: " + pathStr);
			return;
		}
		
		Result result = path._cfBean.getColumn(columnName);
		if(!result.isOk() || !(result.getObject() instanceof ColumnBean)){
				inMessage.setError("Could not find cassandra column description!");
				return;
		}
		
		ColumnBean column = (ColumnBean)result.getObject();
		
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		String inputBoxID = keyspaceName + columnFamilyName;
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", PATH2COLUMN4), 
				keyspaceName,
				columnFamilyName,
				String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
					Constants.makeJSLink(column.getName(), 
							"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value",
							//         1         2   3   4   5   6   7   8   9    10  
							AdminMessageHandler._handler_name, // 1
							resultDivID, // 2
							IMessage._data_action, AdminMessageHandler._action_cs_select_super_column, // 3,4   
							IMessage._data_cs_ksp, keyspaceName,     // 5,6
							IMessage._data_cs_cf, columnFamilyName,     // 7,8
							IMessage._data_cs_key, inputBoxID  // 9,10
							)
/*							,
				Constants.makeJSLink(column.getName(), 
						"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value",
						//         1         2   3   4   5   6   7   8   9  10  11    12  
						AdminMessageHandler._handler_name, // 1
						resultDivID, // 2
						IMessage._data_action, AdminMessageHandler._action_cs_select_column, // 3,4   
						IMessage._data_cs_ksp, keyspaceName,     // 5,6
						IMessage._data_cs_cf, columnFamilyName,     // 7,8
						IMessage._data_cs_col, column.getName(),      // 9,10
						IMessage._data_cs_key, inputBoxID  // 11,12
						)
*/
				)
				+
				String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID)
		);			
//			break;
//		case LINKS:
//			getLog().debug("Create html path for link!");
//			inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", PATH2COLUMN4), 
//					keyspaceName,
//					getKeyspace(keyspaceName).getLinkTableName(),
//					String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
//					Constants.makeJSLink(column.getName(), 
//							"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value", 
//							//         1         2   3   4   5   6   7   8   9  10  11    12  
//							AdminMessageHandler._handler_name, // 1
//							resultDivID, // 2
//							IMessage._data_action, AdminMessageHandler._action_cs_select_column, // 3,4  
//							IMessage._data_cs_ksp, keyspaceName,     // 5,6
//							IMessage._data_cs_cf, columnFamilyName,     // 7,8
//							IMessage._data_cs_col, column.getName(),       // 9,10
//							IMessage._data_cs_key, inputBoxID) // 11,12
//					)
//					+
//					String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID)
//				);			
//			break;
//		default:
//			inMessage.setError(String.format("Uknown super type(%s) for column: %s",
//					column.getTType(),
//					column.getName()));
//			break;
//		}
	}

	public static long getCassandraTimestamp() {
		int factor = 1000; 
		long timestamp = System.currentTimeMillis() * factor;
		return timestamp;
	}

	public static <E> void joinMutationResults(
			String inCfName, 
			String inKeyID,
			List<E> inList,
			Map<String, Map<String, List<E>>> inResult) {
		if(!inResult.containsKey(inKeyID)){
			Map<String, List<E>> newMapCfList = new HashMap<String, List<E>>();
			newMapCfList.put(inCfName, inList);
			inResult.put(inKeyID, newMapCfList);
		}else{
			 Map<String, List<E>> originalMapKeyMapCfList = inResult.get(inKeyID);
			 if(!originalMapKeyMapCfList.containsKey(inCfName)){
				 originalMapKeyMapCfList.put(inCfName, inList);
			 }else{
				 List<E> originalList = originalMapKeyMapCfList.get(inCfName);
				 List<E> newList = new ArrayList<E>(originalList);
				 newList.addAll(inList);
				 originalMapKeyMapCfList.put(inCfName, newList);
				 //for(E e:inList) originalList.add(e);
			 }
		}
		
	}

	public static void debugMutationResult(Map<String, Map<String, List<Mutation>>> result) {
		for(Map.Entry<String, Map<String, List<Mutation>>> entry:result.entrySet()){
			for(Map.Entry<String, List<Mutation>> subEntry: entry.getValue().entrySet()){
				_log.debug(String.format(" ... Cf(%s).Key(%s)", subEntry.getKey(), entry.getKey()));
				for(Mutation mutation:subEntry.getValue()){
					String debugStr = "";
					if(mutation.isSetColumn_or_supercolumn()){
						debugStr += "UPDATE: ";
						if(mutation.column_or_supercolumn.isSetColumn())
							debugStr += "COLUMN: " + DBUtils.bytes2UTF8String(mutation.column_or_supercolumn.column.name, 32);
						else if((mutation.column_or_supercolumn.isSetSuper_column()))
							debugStr += " SUPER COLUMN: " + DBUtils.bytes2UTF8String(mutation.column_or_supercolumn.super_column.name, 32);
						else
							debugStr += " UNKNOWN!!!";
					}else if(mutation.isSetDeletion()){
						debugStr += "DELETE: ";
						if(mutation.deletion.isSetSuper_column())
							debugStr += "SUPER COLUMN: " + DBUtils.bytes2UTF8String(mutation.deletion.super_column, 32);							
						if(mutation.deletion.isSetPredicate()){
							debugStr += " PREDICATE: ";
							if(mutation.deletion.predicate.isSetColumn_names())
								debugStr += " COLUMN NAMES SIZE: " + mutation.deletion.predicate.column_names.size();								
							if(mutation.deletion.predicate.isSetSlice_range())
								debugStr += " SLICE RANGE: " + mutation.deletion.predicate.slice_range;				
						}
					}else
						debugStr += " UNKNOWN!!!";
					_log.debug(" ... ... " + debugStr);
				}
			}
		}
		
	}
	
/* TODO Remove later
	public static CassandraAccessorBean getAccessor() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) BeansUtils.getBean(Constants._beans_cassandra_accessor);
		if(!accessor.isValid()) accessor.setup();
		return accessor;
	}
*/
	
	public static CassandraDescriptorBean getDescriptor() {
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) BeansUtils.getBean(Constants._beans_cassandra_descriptor);
		return descriptor;
	}

	public static void printResult(List<ColumnOrSuperColumn> columns) {
		for(ColumnOrSuperColumn column:columns)
			printResult(column);
		
	}
	
	private static void printResult(ColumnOrSuperColumn superColumn) {
		Assert.assertTrue(superColumn.isSetSuper_column());
		System.out.println(String.format("SuperCol.Name = %s\n",
				DBUtils.bytes2UTF8String(superColumn.super_column.name, 32)));											
		for(Column column:superColumn.getSuper_column().columns){
			System.out.println(String.format("--> Col.Name = %s\n----> Value = %s\n----> Timestamp = %s\n",
					DBUtils.bytes2UTF8String(column.name, 32), 
					DBUtils.bytes2UTF8String(column.value, 32),
					column.timestamp));							
		}
	}

	public static void printResult(ResultAsListOfColumnOrSuperColumn result) {
		if(result.getColumnOrSuperColumn() != null &&
				result.getColumnOrSuperColumn().size() != 0){
			Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
			while(listIterator.hasNext())printResult(listIterator.next());
			_log.debug("Column count: " + result.getColumnOrSuperColumn().size()); 
		}else{
			_log.warn("Nothing to print!");				
		}
	}

	public static String getJSLinkShowAllColumns(KeyspaceBean kspBean,
			ColumnFamilyBean cfBean) {
		return Constants.makeJSLink("Show all",
				"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s'",
				AdminMessageHandler._handler_name,
				AdminMessageHandler._admin_col_divId,
				IMessage._data_action, _action_cs_get_all_columns_id,
				IMessage._data_cs_ksp, kspBean.getName(),
				IMessage._data_cs_cf, cfBean.getName());
	}
	

}
