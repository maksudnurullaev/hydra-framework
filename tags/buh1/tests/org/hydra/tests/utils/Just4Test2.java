package org.hydra.tests.utils;

import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.CountQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.hydra.utils.DBObjects;
import org.hydra.utils.DBUtils;

public class Just4Test2 {
	public static void main(String[] argv){
//		Map<String, Map<String, String>> rows = DBObjects.getObjects("Test", 5, "Object2 2012.10.16 14:00:22 699 8f", "Object2 2012.10.16 15:12:21 406 3d");
//		Map<String, Map<String, String>> rows = DBObjects.getObjects("Test", "Object2");
//		System.out.println(String.format("rows.size(): %s", rows.size()));
//		for(String key:rows.keySet()){
//			System.out.println(String.format("... key: %s", key));
//			if(rows.get(key) == null) { continue; }
//			for(String subKey: rows.get(key).keySet()){
//				System.out.println(String.format("... ... %s: %s", subKey, rows.get(key).get(subKey)));
//			}
//		}
		Keyspace keyspace = DBUtils.getKspManager().getKeyspace("Test");
		StringSerializer ss = StringSerializer.get();
		RangeSlicesQuery<String,String,String> rqs = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
		rqs.setColumnFamily("Objects");
		rqs.setKeys(null, null);
		rqs.setRowCount(10000);
		rqs.setRange(null, null, false, 10);
		QueryResult<OrderedRows<String, String, String>> qr = rqs.execute();
		int valid_row_count = 0;
		for(Row<String, String, String> row:qr.get().getList()){
        	if(row.getColumnSlice() != null 
        			&& row.getColumnSlice().getColumns() != null
        			//&& row.getColumnSlice().getColumns().size() > 0
        			){
        		valid_row_count += 1;
        	}			
//			for(HColumn<String, String> c: row.getColumnSlice().getColumns()){
//				System.out.println(String.format("... ... %s: %s", c.getName(), c.getValue()));
//			}
		}
		System.out.println("Records count: " + qr.get().getCount());
		System.out.println("Valid records count: " + valid_row_count);
				
//		List<Row<String, String, String>> list = DBUtils.getRows("Test", "Objects", 1, null, null, "Object3", "Object3");
//		System.out.println("Records count: " + list.size());
//		for(Row<String, String, String> row: list){
//			System.out.println(String.format("... key: %s", row.getKey()));
//			for(HColumn<String, String> c: row.getColumnSlice().getColumns()){
//				System.out.println(String.format("... ... %s: %s", c.getName(), c.getValue()));
//			}
//			
//		}
	}
}
