package org.hydra.tests.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.Mutation;
import org.hydra.utils.DBUtils;


public class Just4Run {

	
	public static void main(String[] args) {
//		CassandraVirtualPath path = new CassandraVirtualPath(DBUtils.getDescriptor(), Utils4Tests.KSMAINTEST_Users);
//		
//		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
//		Assert.assertTrue(path._kspBean != null);
//		Assert.assertTrue(path._cfBean != null);
//		
//		DBUtils.getAccessor().delete(path);		
		
		remove("KSMainTEST", "Links", "user0", "Articles");
		
//		Client client = DBUtils.getAccessor().clientBorrow();
//		
//		Map<String, Map<String, List<Mutation>>> result = 
//			new HashMap<String, Map<String,List<Mutation>>>();	
//		
//		Map<String, List<Mutation>> resultSub = new HashMap<String, List<Mutation>>();
//		
//		List<Mutation> listMut = new ArrayList<Mutation>();
//		
//		Mutation mutation = new Mutation();
//		
//		Deletion deletion = new Deletion(DBUtils.getCassandraTimestamp());
//		
//		deletion.setSuper_column(DBUtils.string2UTF8Bytes("Articles"));
//		// deletion.setPredicate(DBUtils.getSlicePredicateStr("article0"));
//		
//		
//		mutation.setDeletion(deletion);
//		
//		listMut.add(mutation);
//		
//		resultSub.put("Links", listMut);
//		
//		result.put("user0", resultSub);
//		
//		
//		try {
//			client.batch_mutate(
//					"KSMainTEST", 
//					result,
//					ConsistencyLevel.ONE);
//			
//		} catch (Exception e) {
//			System.out.println(e);
//		} finally {
//			DBUtils.getAccessor().clientRelease(client);
//		}		
		
	}
	
	public static void remove(String inKsp, String inCf, String inKey, String inSuper){
		Client client = DBUtils.getAccessor().clientBorrow();
		
		Map<String, Map<String, List<Mutation>>> result = 
			new HashMap<String, Map<String,List<Mutation>>>();	
		
		Map<String, List<Mutation>> resultSub = new HashMap<String, List<Mutation>>();
		
		List<Mutation> listMut = new ArrayList<Mutation>();
		
		Mutation mutation = new Mutation();
		
		Deletion deletion = new Deletion(DBUtils.getCassandraTimestamp());
		
		deletion.setSuper_column(DBUtils.string2UTF8Bytes(inSuper));		
		
		mutation.setDeletion(deletion);
		
		listMut.add(mutation);
		
		resultSub.put(inCf, listMut);
		
		result.put(inKey, resultSub);		
		
		try {
			client.batch_mutate(
					inKey, 
					result,
					ConsistencyLevel.ONE);
			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			DBUtils.getAccessor().clientRelease(client);
		}				
	}

}