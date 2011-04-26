package org.hydra.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author M.Nurullayev
 * 
 */
public final class DBUtils {
	private static final Log _log = LogFactory.getLog("org.hydra.utils.DBUtils");

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

	public static Result getFromKey(
			String inWhat, 
			String inKey,
			String inApplicationID,
			String inСolumnName) {
		
		String appIdCfBean = "cf" + inApplicationID + inWhat;
		
		_log.debug("Try to find bean: " + appIdCfBean);		
		Result result = new Result();
		BeansUtils.getWebContextBean(result , appIdCfBean);
		
		if(result.isOk()){
			if(result.getObject() instanceof SimpleCassandraDao){	
				SimpleCassandraDao s = (SimpleCassandraDao) result.getObject();
				_log.debug("... try to find value by key: " + inKey);
				String temp = s.get(inKey, inСolumnName);
				if(temp != null){
					_log.debug("... found!");
					result.setObject(temp);
					result.setResult(true);
					
				}else{
					result.setResult("... exception: seems not found data in: " + appIdCfBean);
					result.setResult(false);
				}
			}else{
				_log.error("Bean is not istance of SimpleCassandraDao!");
				result.setResult("Bean is not istance of SimpleCassandraDao!");
				result.setResult(false);
			}
		}else{
			_log.error("Bean is not istance of SimpleCassandraDao!");
			result.setResult("Bean is not istance of SimpleCassandraDao!");
			result.setResult(false);			
		}
		return result;
	}
}
