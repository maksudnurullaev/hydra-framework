package org.hydra.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.beans.ColumnFamilyBean;
import org.junit.Assert;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class DBUtils {
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

	public static Map<byte[], Map<byte[], byte[]>> convertMapByteAMapByteAByteA(
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
		if(result.getColumnOrSuperColumn() != null ||
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
			System.out.println("TEST COLUMN COUNT: " + result.getColumnOrSuperColumn().size()); 
		}else{
			System.out.println("Result is NULL or EMPTY!");				
		}
	}

	public static boolean validateCfAndMap(ColumnFamilyBean cf,
			Map<String, ?> inMap) {
		if(cf == null || inMap == null){
			_log.error("Invalid Cf or Map: NULL!");
			return false;
		}
		for(String key:inMap.keySet()){
			if(!cf.getColumns().containsKey(key)){
				_log.error(String.format("Could not find column(%s) for cf(%s)", key, cf.getName()));
				return false;
			}
		}
		return true;
	}
	
}
