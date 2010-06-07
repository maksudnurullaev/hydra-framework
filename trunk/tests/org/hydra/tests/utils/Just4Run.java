package org.hydra.tests.utils;

import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.utils.Constants;
import org.hydra.utils.DBUtils;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.springframework.beans.factory.BeanFactory;


public class Just4Run {

	public static final String UTF8 = "UTF8";

	public static void main(String[] args) {
		BeanFactory beanFactory = Utils4Tests.getBeanFactory();
		CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
		CassandraAccessorBean accessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
		CassandraVirtualPath testUsersPath = new CassandraVirtualPath(descriptor, "KSMainTEST.Articles");
		
		if(!accessor.isValid())
			accessor.setup();
		
		ResultAsListOfColumnOrSuperColumn resultUsers = accessor.get4KspCf(testUsersPath);
	
		DBUtils.printResult(resultUsers);
	}
}
