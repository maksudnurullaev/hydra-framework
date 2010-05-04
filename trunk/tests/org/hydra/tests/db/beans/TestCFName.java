package org.hydra.tests.db.beans;

import java.util.HashMap;
import java.util.Map;

import org.hydra.db.beans.Key;
import org.hydra.db.beans.Cf;
import org.hydra.db.beans.Key.SUPER;
import org.junit.Assert;
import org.junit.Test;

public class TestCFName {
	
	@Test
	public void test_type(){
		Cf cf = new Cf();
		Map<String, Key> cfkeys = new HashMap<String, Key>();
		
		Key field1 = new Key();				
		field1.setSuper("COLUMNS");
		cfkeys.put("TypeCOLUMN", field1);

		Key field3 = new Key();				
		field3.setSuper("LINKS");
		cfkeys.put("TypeLINKS", field3);
		
		cf.setKeys(cfkeys);
		
		Assert.assertEquals(SUPER.COLUMNS, cf.getKeys().get("TypeCOLUMN").getSuper());
		Assert.assertEquals(SUPER.LINKS, cf.getKeys().get("TypeLINKS").getSuper());
		
	}	
}
