package org.hydra.tests.utils;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.tests.db.fmd.TestFMDKspCfIdLinks;
import org.hydra.utils.DBUtils;

public class Just4Run {
	static String userID = "user0";
	static CassandraAccessorBean accessor = DBUtils.getAccessor();
	static CassandraDescriptorBean descriptor = DBUtils.getDescriptor();
	
	public static void main(String[] args) {
		TestFMDKspCfIdLinks.initTestDataStage1User();
		TestFMDKspCfIdLinks.initTestDataStage2Articles();
		
//		CassandraVirtualPath path2Delete = new CassandraVirtualPath(descriptor,
//				Utils4Tests.KSMAINTEST_Users + "." + userID);		
//		
//		System.out.println("Delete2path: " + path2Delete.getPath());
//		
//		accessor.delete(path2Delete);
//		// Mutation2Delete.generate(path2Delete);
		
		// ############# delete cascade
//		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, 
//				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, "user0"));
		
//		// init single user for test
//		Map<String, Map<String, String>> user = Utils4Tests.initTestUsers(1);
//		CassandraVirtualPath path = new CassandraVirtualPath(Utils4Tests.getDescriptor(),
//				Utils4Tests.KSMAINTEST_Users);		
//		Result result = Utils4Tests.getAccessor().update(path, DBUtils.convert2Bytes(user));				
//		Assert.assertTrue(result.isOk());
//		
//		result = Utils4Tests.getAccessor().delete(path);		
//		Assert.assertTrue(result.isOk());
	}

}