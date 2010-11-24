package org.hydra.tests.bean;

import org.hydra.beans.WebApplications;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.BeforeClass;

public class Test0 {
	protected static CassandraDescriptorBean _cassandraDescriptor = BeansUtils4Tests.getDescriptor();
	protected static CassandraAccessorBean _cassandraAccessor = BeansUtils4Tests.getAccessor();
	protected static WebApplications _hydra_applicatons = BeansUtils4Tests.getWebAppsMngr();
	
	@BeforeClass
	public static void test_0(){		
		Assert.assertNotNull(_hydra_applicatons);
		Assert.assertTrue(_hydra_applicatons instanceof WebApplications);
		
		Assert.assertNotNull(_cassandraDescriptor);
		Assert.assertTrue(_cassandraDescriptor instanceof CassandraDescriptorBean);
		
		Assert.assertNotNull(_cassandraAccessor);
		Assert.assertTrue(_cassandraAccessor instanceof CassandraAccessorBean);
	}	
	
}
