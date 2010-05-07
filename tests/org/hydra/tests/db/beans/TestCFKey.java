package org.hydra.tests.db.beans;

import org.hydra.db.beans.ColumnBean;
import org.junit.Assert;
import org.junit.Test;

public class TestCFKey {
	
	@Test
	public void test_type(){
		ColumnBean key = new ColumnBean();		
		Assert.assertNull(key.getSuper());
		
		key.setSuper("COLUMNS");
		Assert.assertEquals(key.getSuper(), "COLUMNS");
				
		key.setSuper("LINKS");
		Assert.assertEquals(key.getSuper(), "LINKS");

	}
	
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test_to_error(){
		ColumnBean field = new ColumnBean();		
		field.setSuper("HELLO");		
	}
	
	
}
