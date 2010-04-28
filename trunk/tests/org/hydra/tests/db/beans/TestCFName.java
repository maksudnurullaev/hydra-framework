package org.hydra.tests.db.beans;

import java.util.HashMap;
import java.util.Map;

import org.hydra.db.beans.CFKey;
import org.hydra.db.beans.CFName;
import org.hydra.db.beans.CFKey.TYPE;
import org.junit.Assert;
import org.junit.Test;

public class TestCFName {
	
	@Test
	public void test_type(){
		CFName cf = new CFName();
		Map<String, CFKey> cfkeys = new HashMap<String, CFKey>();
		
		CFKey field1 = new CFKey();				
		field1.setType("COLUMN");
		cfkeys.put("TypeCOLUMN", field1);

		CFKey field3 = new CFKey();				
		field3.setType("LINKS");
		cfkeys.put("TypeLINKS", field3);
		
		cf.setFields(cfkeys);
		
		Assert.assertEquals(TYPE.COLUMNS, cf.getFields().get("TypeCOLUMN").getType());
		Assert.assertEquals(TYPE.LINKS, cf.getFields().get("TypeLINKS").getType());
		
	}	
}
