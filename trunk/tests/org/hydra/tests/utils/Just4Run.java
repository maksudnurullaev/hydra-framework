package org.hydra.tests.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;

public class Just4Run {

	public static void main(String[] args) {
		
		Client client = Utils4Tests.getAccessor().clientBorrow();

		Result result = new Result();

		ColumnPath cf = new ColumnPath("Articles");
		// cf.setSuper_column(DBUtils.string2UTF8Bytes("articleTest"));
		System.out.println("Utils4Tests.getAccessor().isValid(): " + Utils4Tests.getAccessor().isValid());
		System.out.println("cf.isSetSuper_column(): " + cf.isSetSuper_column());

//		ColumnPath cf = new ColumnPath("Users");
//		cf.setSuper_column(DBUtils.string2UTF8Bytes("testID"));
//		System.out.println("cf.isSetSuper_column(): " + cf.isSetSuper_column());		
		
		CassandraVirtualPath path = new CassandraVirtualPath(Utils4Tests.getDescriptor(), 
				"KSMainTEST.Articles");
		
		ResultAsListOfColumnOrSuperColumn resultFind = Utils4Tests.getAccessor().find(path);
		Assert.assertTrue(resultFind.isOk());
		
		long vlong = resultFind.getColumnOrSuperColumn().get(0).super_column.columns.get(0).timestamp;
		
		DBUtils.printResult(resultFind);
//		
//		Map<String, Map<String, List<Mutation>>> mutation_map = new HashMap<String, Map<String, List<Mutation>>>();
//		Map<String, List<Mutation>> map = new HashMap<String, List<Mutation>>();
//		List<Mutation> list = new ArrayList<Mutation>();
//		Mutation mutation = new Mutation();
//		Deletion deletion = new Deletion();
//		deletion.timestamp = vlong; //System.currentTimeMillis();
//		deletion.setPredicate(DBUtils.getSlicePredicate(null, null));
//		//deletion.setSuper_column(DBUtils.string2UTF8Bytes("articleTest"));
//		//deletion.setColumn(DBUtils.string2UTF8Bytes("articleTest"));
//		mutation.setDeletion(deletion);
//		list.add(mutation);
//		map.put("Articles", list);
//		mutation_map.put("COLUMN", map);
//		
//		map.put("Articles", list);
		
		try {
			client.remove("KSMainTEST", "COLUMNS", cf, vlong , ConsistencyLevel.ONE);
			// client.remove("KSMainTEST", "COLUMNS", cf, System.currentTimeMillis() , ConsistencyLevel.ONE);
			// client.remove("KSMainTEST", "COLUMNS", cf, 0, ConsistencyLevel.ONE);
			// client.batch_mutate("KSMainTEST", mutation_map, ConsistencyLevel.ONE);
			result.setResult(true);
			result.setResult(null);
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			result.setResult(false);
			result.setResult(e.toString());
		} finally {
			Utils4Tests.getAccessor().clientRelease(client);
		}		

		if(!result.isOk()){
			System.out.println(result.getResult());
			System.exit(1);
		}else System.out.println("Done!");
		
		
//		Result result = Utils4Tests.getAccessor().delete("KSMainTEST", cf , "COLUMNS");
//		
//		
		CassandraVirtualPath path2 = new CassandraVirtualPath(Utils4Tests.getDescriptor(), 
				//"KSMainTEST.Users.user0");
				//"KSMainTEST.Users");
				"KSMainTEST.Articles.");
				//"KSMainTEST.Articles.articleTest");
		
//		Result resultDel = 
//			Utils4Tests.getAccessor().delete(path);
//		Assert.assertTrue(resultDel.isOk());
		
		ResultAsListOfColumnOrSuperColumn resultFind2 = Utils4Tests.getAccessor().find(path2);
		Assert.assertTrue(resultFind2.isOk());
		
		DBUtils.printResult(resultFind2);
		
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