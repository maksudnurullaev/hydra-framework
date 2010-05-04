package org.hydra.tests.db.beans;

import org.hydra.db.beans.Key;
import org.hydra.db.beans.Key.SUPER;
import org.junit.Assert;
import org.junit.Test;

public class TestCFKey {
	
	@Test
	public void test_type(){
		Key key = new Key();		
		Assert.assertNull(key.getSuper());
		
		key.setSuper("COLUMNS");
		Assert.assertEquals(key.getSuper(), SUPER.COLUMNS);
				
		key.setSuper("LINKS");
		Assert.assertEquals(key.getSuper(), SUPER.LINKS);

	}
	
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test_to_error(){
		Key field = new Key();		
		field.setSuper("HELLO");		
	}
	
	
}
