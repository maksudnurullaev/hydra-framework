package org.hydra.tests.utils;

import java.util.Iterator;
import java.util.Set;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.hydra.db.beans.ColumnFamilyBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;

public class Just4Run {

	public static void main(String[] args) {
		CassandraVirtualPath path = new CassandraVirtualPath(Utils4Tests.getDescriptor(), 
				Utils4Tests.KSMAINTEST_Users + ".user0");
		
		Result result = 
			Utils4Tests.getAccessor().delete(path);
		
		Assert.assertTrue(result.isOk());
		
//		Set<ColumnFamilyBean> childs = path._cfBean.getChilds();
//		System.out.println(childs.contains("Articles"));
//		if(childs != null){
//			for(ColumnFamilyBean cfb: childs){
//				System.out.println(cfb.getName());
//			}
//		}
		
//		ResultAsListOfColumnOrSuperColumn result = Utils4Tests.getAccessor().getAllLinks4(path,
//				//DBUtils.getSlicePredicate("Articles", "Articles"));
//				DBUtils.getSlicePredicate("TestLink", "TestLink"));
//				//DBUtils.getSlicePredicate(null, null));
//		
//		if(result.getColumnOrSuperColumn() != null &&
//				result.getColumnOrSuperColumn().size() != 0){
//			Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
//			while(listIterator.hasNext()){
//				ColumnOrSuperColumn superColumn = listIterator.next();
//				Assert.assertTrue(superColumn.isSetSuper_column());
//				System.out.println(String.format("SuperCol.Name = %s\n",
//						DBUtils.bytes2UTF8String(superColumn.super_column.name, 32)));											
//				for(Column column:superColumn.getSuper_column().columns){
//					System.out.println(String.format("--> Col.Name = %s\n----> Value = %s\n----> Timestamp = %s\n",
//							DBUtils.bytes2UTF8String(column.name, 32), 
//							DBUtils.bytes2UTF8String(column.value, 32),
//							column.timestamp));							
//				}
//			}
//			System.out.println("Column count: " + result.getColumnOrSuperColumn().size()); 
//		}else{
//			System.out.println("Nothing to print!");				
//		}
	}

}