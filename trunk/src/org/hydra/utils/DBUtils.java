package org.hydra.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
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
	
	public static void printResult(ResultAsListOfColumnOrSuperColumn result) {
		if(result.getColumnOrSuperColumn() != null &&
				result.getColumnOrSuperColumn().size() != 0){
			Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
			while(listIterator.hasNext()){
				ColumnOrSuperColumn superColumn = listIterator.next();
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
			_log.debug("Column count: " + result.getColumnOrSuperColumn().size()); 
		}else{
			_log.warn("Nothing to print!");				
		}
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
	
	public static SlicePredicate getSlicePredicate(String inStartSliceRange, String inFinishSliceRange){
		// setup slice range
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange();
        
        if(inStartSliceRange == null){
	        sliceRange.setStart(new byte[0]);
        }else{
	        sliceRange.setStart(string2UTF8Bytes(inStartSliceRange));        	
        }
        
        if(inFinishSliceRange == null){
        	sliceRange.setFinish(new byte[0]);
        }else{
        	sliceRange.setFinish(string2UTF8Bytes(inFinishSliceRange));
        }	

        predicate.setSlice_range(sliceRange);		
		
		return predicate;		
	}
	
	public static void describeColumn(CassandraDescriptorBean descriptor, IMessage inMessage){
		String keyspaceName = inMessage.getData().get(IMessage._data_cs_ksp);
		String columnFamilyName = inMessage.getData().get(IMessage._data_cs_cf); 
		String columnName = inMessage.getData().get(IMessage._data_cs_col); 
		
		String pathStr = String.format("%s.%s", keyspaceName, columnFamilyName);
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, pathStr);
		
		if(!path.isValid()){
			inMessage.setError("Invalid path!");
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
		inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", PATH2COLUMN5), 
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
							),
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
}
