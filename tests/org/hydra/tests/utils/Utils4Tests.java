package org.hydra.tests.utils;

import java.util.HashMap;
import java.util.Map;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.BeansUtils;
import org.junit.Assert;

/**
 * To use this test we should create 1000 test users
 * 
 * @see Create100TestUsers
 * @author M.Nurullayev
 */
public final class Utils4Tests {
	public static final String USER_S = "user%s";
	public static final String USER_PASSWORD_S = "Password4%s";
	public static final String USER_PASSWORD = "Password";
	public static final String USER_EMAIL = "Email";
	public static final String USER_TEST_S_MAIL_COM = "test%s@mail.com";
	public static final String KSMAINTEST_Users = "KSMainTEST.Users";
	public static final String KSMAINTEST_Articles = "KSMainTEST.Articles";
	public static final String KSMAINTEST_Users_S_Articles = "KSMainTEST.Users.%s.Articles";
	
	private static final String ARTICLE_S = "article%s";
	private static final String ARTICLE_TITLE_S = "article title %s";
	private static final String ARTICLE_TEXT_S = "article text %s";
	private static final String ARTICLE_TITLE = "Title";
	private static final String ARTICLE_TEXT = "Text";
	
	public static Map<String, Map<String, String>> initTestUsers(int count) {
		
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		
		String userID = null;
		Map<String, String> tempMap = null;
		for (int i = 0; i < count; i++) {
			userID = String.format(USER_S, i); //Constants.GetDateUUIDTEST();
			
			tempMap = new HashMap<String, String>();
			
			tempMap.put(USER_PASSWORD, String.format(USER_PASSWORD_S, userID)); // CryptoManager.encryptPassword(userID));
			tempMap.put(USER_EMAIL, String.format(USER_TEST_S_MAIL_COM, i));
			
			result.put(userID, tempMap);
		}

		return result;
	}

	public static Map<String, Map<String, String>> initTestArticles(int count) {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		
		CassandraAccessorBean accessor = (CassandraAccessorBean) BeansUtils.getBean(Constants._beans_cassandra_accessor);
		if(!accessor.isValid())accessor.setup();
		
		// 1. Iterate over the user count and create Map<String, Map<String,String>> for batch insert
		String articleID = null;
		Map<String, String> tempMap = null;
		for (int i = 0; i < count; i++) {
			articleID = String.format(ARTICLE_S, i);

			tempMap = new HashMap<String, String>();
			
			tempMap.put(ARTICLE_TITLE, String.format(ARTICLE_TITLE_S, articleID )); 
			tempMap.put(ARTICLE_TEXT, String.format(ARTICLE_TEXT_S, Constants.GetCurrentDateTime()));
			
			result.put(articleID, tempMap);
		}		
		
		return result;
	}
	
	public static Result deleteAllTestUsers() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) BeansUtils.getBean(Constants._beans_cassandra_accessor);
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) BeansUtils.getBean(Constants._beans_cassandra_descriptor);
		
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSMAINTEST_Users);
		
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		return accessor.delete(path);		
	}
	
	public static Result deleteAllTestArticles() {
		CassandraAccessorBean accessor = (CassandraAccessorBean) BeansUtils.getBean(Constants._beans_cassandra_accessor);
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) BeansUtils.getBean(Constants._beans_cassandra_descriptor);
		
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSMAINTEST_Articles);
		
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path._kspBean != null);
		Assert.assertTrue(path._cfBean != null);
		
		return accessor.delete(path);		
	}	
}
