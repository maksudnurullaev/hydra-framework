package org.hydra.tests.utils;

import java.util.Map;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.springframework.beans.factory.BeanFactory;


public class Just4Run {

	public static void main(String[] args) {
		Map<String, Map<String, String>> result = Utils4Tests.initTestUsers(3);
		printUsers();
		//TODO test deletion actual result one by one
		
		Utils4Tests.deleteAllTestUsers();
		printUsers();
		
	}

	private static void printUsers() {
		BeanFactory beanFactory = Utils4Tests.getBeanFactory();
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
		CassandraAccessorBean accessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
		CassandraVirtualPath testUsersPath = new CassandraVirtualPath(descriptor, "KSMainTEST.Users");
		
		if(!accessor.isValid())
			accessor.setup();
		
		ResultAsListOfColumnOrSuperColumn resultUsers = accessor.get4KspCf(testUsersPath);
	
		DBUtils.printResult(resultUsers);	
	}
}
