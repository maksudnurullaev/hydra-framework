package org.hydra.tests.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.Mutation;
import org.hydra.tests.db.fmd.TestFMDKspCfIdLinks;
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
		
//		TestFMDKspCfIdLinks.initTestDataStage1User();
//		TestFMDKspCfIdLinks.initTestDataStage2Articles();
		
		// delete4KspCfKeySuper("KSMainTEST", "Links", "user0", "Articles");
		 delete4KspCfKeySuperCol("KSMainTEST", "Links", "user0", "Articles", "article0");
		 delete4KspCfKeySuper("KSMainTEST", "Links", "user0", "Articles");
		 delete4KspCfKey("KSMainTEST", "Links", "user0");
		
		System.out.println("Done!");
		
	}
	
//	public static void delete4KspCfKeySuper(String inKsp, String inCf, String inKey, String inSuper){
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
//		deletion.setSuper_column(DBUtils.string2UTF8Bytes(inSuper));
//		//TODO [we expect it at Cassadnras 0.7 release] deletion.setPredicate(DBUtils.getSlicePredicateStr("article0"));
//		
//		
//		mutation.setDeletion(deletion);
//		
//		listMut.add(mutation);
//		
//		resultSub.put(inCf, listMut);
//		
//		result.put(inKey, resultSub);
//				
//		try {
//			client.batch_mutate(
//					inKsp, 
//					result,
//					ConsistencyLevel.ONE);
//			
//		} catch (Exception e) {
//			System.out.println(e);
//		} finally {
//			DBUtils.getAccessor().clientRelease(client);
//		}						
//	}
	
	public static void delete4KspCfKeySuperCol(String inKsp, String inCf, String inKey, String inSuper, String inCol){
		Client client = DBUtils.getAccessor().clientBorrow();
		
		ColumnPath cfPath = new ColumnPath(inCf);
		
		if(inSuper != null)cfPath.setSuper_column(DBUtils.string2UTF8Bytes(inSuper));
		if(inCol != null) cfPath.setColumn(DBUtils.string2UTF8Bytes(inCol));
		
		
		try {
			client.remove(inKsp, inKey, cfPath , DBUtils.getCassandraTimestamp(), ConsistencyLevel.ONE);			
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			DBUtils.getAccessor().clientRelease(client);
		}
	}
	
	public static void delete4KspCfKeySuper(String inKsp, String inCf, String inKey, String inSuper){
		delete4KspCfKeySuperCol(inKsp, inCf, inKey, inSuper, null);
	}	

	public static void delete4KspCfKey(String inKsp, String inCf, String inKey){
		delete4KspCfKeySuperCol(inKsp, inCf, inKey, null, null);
	}
}