package org.hydra.tests.db.beans;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.ColumnBean.COLUMN_TYPES;
import org.hydra.messages.interfaces.IMessage;
import org.junit.Assert;
import org.junit.Test;

public class TestCFKey {
	
	@Test
	public void test_type(){
		ColumnBean key = new ColumnBean();		
		Assert.assertEquals(key.getTType(), COLUMN_TYPES.UNDEFINED);
		
		key.setType("COLUMN");
		Assert.assertEquals(key.getTType(), COLUMN_TYPES.COLUMNS);
				
		key.setType("LINK");
		Assert.assertEquals(key.getTType(), COLUMN_TYPES.LINKS);

	}
	
	public void test_to_error(){
		ColumnBean field = new ColumnBean();		
		field.setType("HELLO");		
		Assert.assertNull(field.getTType());
	}
	
	
}
