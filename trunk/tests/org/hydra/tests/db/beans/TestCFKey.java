package org.hydra.tests.db.beans;

import org.hydra.db.beans.CFKey;
import org.hydra.db.beans.CFKey.TYPE;
import org.junit.Assert;
import org.junit.Test;

public class TestCFKey {
	
	@Test
	public void test_type(){
		CFKey field = new CFKey();		
		Assert.assertNull(field.getType());
		
		field.setType("COLUMNS");
		Assert.assertEquals(field.getType(), TYPE.COLUMNS);
				
		field.setType("LINKS");
		Assert.assertEquals(field.getType(), TYPE.LINKS);

	}
	
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test_to_error(){
		CFKey field = new CFKey();		
		field.setType("HELLO");		
	}
	
	
}
