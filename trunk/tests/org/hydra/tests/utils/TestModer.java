package org.hydra.tests.utils;

import org.hydra.utils.Moder;
import org.hydra.utils.Moder.MODE;
import org.junit.Assert;
import org.junit.Test;


public class TestModer {
	private static final String inContent = "http://127.0.0.1:8181/hydra-0.2a/?mode=template&id=html.body.top&sdfds";

	@Test
	public void test_invalid() {
		Moder moder = new Moder("");
		Assert.assertTrue(moder.getMode() == MODE.MODE_UKNOWN);
		Assert.assertNull(moder.getId());
	}
	
	@Test
	public void test_invalid2() {
		Moder moder = new Moder(null);
		Assert.assertTrue(moder.getMode() == MODE.MODE_UKNOWN);
		Assert.assertNull(moder.getId());
	}
	
	@Test
	public void test_valid() {
		Moder moder = new Moder(inContent);
		Assert.assertTrue(moder.getMode() == MODE.MODE_TEMPLATE);
		Assert.assertNotNull(moder.getId());
	}
	
}