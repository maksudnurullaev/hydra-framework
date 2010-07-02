package org.hydra.tests.utils;

import java.util.Map;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;

public class Just4Run {
	static String userID = "user0";
	static CassandraAccessorBean accessor = DBUtils.getAccessor();
	static CassandraDescriptorBean descriptor = DBUtils.getDescriptor();
	
	public static void main(String[] args) {
		initTestData();
		
		CassandraVirtualPath path2Delete = new CassandraVirtualPath(descriptor,
				Utils4Tests.KSMAINTEST_Users + "." + userID);		
		
		System.out.println("Delete2path: " + path2Delete.getPath());
		
		accessor.delete(path2Delete);
		// Mutation2Delete.generate(path2Delete);
		
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

	public static void initTestData() {

		// ############### init test data
		// init USER
		Map<String, Map<String, String>> user = Utils4Tests.initTestUsers(1);
		userID = (String) user.keySet().toArray()[0];
		CassandraVirtualPath path2Users = new CassandraVirtualPath(descriptor,
				Utils4Tests.KSMAINTEST_Users);
		Result result = accessor.update(path2Users, DBUtils.convert2Bytes(user));
		
		if(result.isOk())
			System.out.println("Initial user data merged!");
		else
			System.out.println(result.getResult());
		
		Map<String, Map<String, String>> articles = 
			Utils4Tests.initTestArticles(15);		
		// init linked ARTICLES
		CassandraVirtualPath path2UsersIDArticles = new CassandraVirtualPath(
				descriptor,
				String.format(Utils4Tests.KSMAINTEST_Users_S_Articles, userID));
		result = accessor.update(path2UsersIDArticles, DBUtils
				.convert2Bytes(articles));
		
		if(result.isOk())
			System.out.println("Initial linked articles data merged!");
		else
			System.out.println(result.getResult());
	}

}