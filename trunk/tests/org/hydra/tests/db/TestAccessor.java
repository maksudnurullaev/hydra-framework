package org.hydra.tests.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.db.server.CassandraVirtualPath.ERR_CODES;
import org.hydra.tests.utils.Utils4Tests;
import org.hydra.utils.Constants;
import org.hydra.utils.CryptoManager;
import org.hydra.utils.DBUtils;
import org.hydra.utils.Result;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;

public class TestAccessor {
	Log _log = LogFactory.getLog(this.getClass());
	BeanFactory beanFactory = Utils4Tests.getBeanFactory();
	CassandraAccessorBean accessor = (CassandraAccessorBean) beanFactory.getBean(Constants._beans_cassandra_accessor);
	CassandraDescriptorBean descriptor = (CassandraDescriptorBean) beanFactory.getBean(Constants._beans_cassandra_descriptor);
	
	public static final String KSTestUserPath = "KSMainTEST.Users";

	@Before
	public void before(){
		Assert.assertNotNull(beanFactory);
		Assert.assertNotNull(_log);
		Assert.assertNotNull(accessor);
		Assert.assertNotNull(descriptor);
		
		if(!accessor.isValid())
			accessor.setup();
	}
	
	@Test
	public void test_getIDs(){
		Assert.assertTrue(accessor.isValid());
		
		clearTestUsers();
		initTestUsers(5);
		
		/* Test accessor */
		CassandraVirtualPath testPath = new CassandraVirtualPath(descriptor, KSTestUserPath);
		ResultAsListOfColumnOrSuperColumn result = accessor.resultAsListOfColumns4KspCf(testPath);
		
		Assert.assertTrue(result.isOk());
		if(result.getColumnOrSuperColumn() != null ||
				result.getColumnOrSuperColumn().size() != 0){
			Iterator<ColumnOrSuperColumn> listIterator =  result.getColumnOrSuperColumn().iterator();
			while(listIterator.hasNext()){
				ColumnOrSuperColumn superColumn = listIterator.next();
				Assert.assertTrue(superColumn.isSetSuper_column());
				System.out.println(String.format("SuperCol.Name = %s\n",
						DBUtils.bytes2UTF8String(superColumn.super_column.name)));											
				for(Column column:superColumn.getSuper_column().columns){
					System.out.println(String.format("--> Col.Name = %s\n----> Value = %s\n----> Timestamp = %s\n",
							DBUtils.bytes2UTF8String(column.name, 32), 
							DBUtils.bytes2UTF8String(column.value, 32),
							column.timestamp));							
				}
			}
			System.out.println("TEST COLUMN COUNT: " + result.getColumnOrSuperColumn().size()); 
		}else{
			System.out.println("Result is NULL or EMPTY!");				
		}
		clearTestUsers();
	}

	private void clearTestUsers() {
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUserPath);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		
		accessor.batchDelete4KspCf(path);
	}

	private Map<byte[], Map<byte[],byte[]>> initTestUsers(int count) {		
		Map<byte[], Map<byte[],byte[]>> result = new HashMap<byte[], Map<byte[],byte[]>>();
		
		// 0. test
		if(count == 0) return null;
		
		// 1. Iterate over the user count and create Map<String, Map<String,String>> for batch insert
		String userID = null;
		Map<byte[],byte[]> tempMap;
		for (int i = 0; i < count; i++) {
			userID = Constants.GetDateUUIDTEST();
			System.out.println(userID);

			tempMap = new HashMap<byte[],byte[]>();
			
			tempMap.put(DBUtils.string2UTF8Bytes("Password"), 
					DBUtils.string2UTF8Bytes(CryptoManager.encryptPassword(userID)));
			tempMap.put(DBUtils.string2UTF8Bytes("Email"), 
					DBUtils.string2UTF8Bytes(String.format("test%s@mail.com", i)));
			
			result.put(DBUtils.string2UTF8Bytes(userID), tempMap);
		}
		
		// 2. Create access path for batch insert
		CassandraVirtualPath path = new CassandraVirtualPath(descriptor, KSTestUserPath);
		Assert.assertEquals(path.getErrorCode(), ERR_CODES.NO_ERROR); 
		Assert.assertTrue(path.kspBean != null);
		Assert.assertTrue(path.cfBean != null);
		
		// 3. Send Map<String, Map<String,String>> to batch insert
		Result batchInsertResult = accessor.batchMutate(path, result);
		
		// 4. Test result
		Assert.assertTrue(batchInsertResult.isOk());
		
		
		return result;		
	}

}
