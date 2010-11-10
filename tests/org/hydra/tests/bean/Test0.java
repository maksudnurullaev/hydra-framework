package org.hydra.tests.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.tests.utils.BeansUtils4Tests;
import org.junit.Assert;
import org.junit.Test;

public class Test0 {
	protected Log _log = LogFactory.getLog(this.getClass());
	protected static CassandraDescriptorBean _cassandraDescriptor = BeansUtils4Tests.getDescriptor();
	protected static CassandraAccessorBean _cassandraAccessor = BeansUtils4Tests.getAccessor();
	
	@Test
	public void test_0_beans(){
		Assert.assertNotNull(_log);
		Assert.assertNotNull(_cassandraDescriptor);
		Assert.assertNotNull(_cassandraAccessor);
	}
	
}
