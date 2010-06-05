package org.hydra.tests.utils;

import junit.framework.Assert;

import org.hydra.utils.ByteArray;
import org.junit.Test;

public class TestByteArray {
	byte[] ba_null = null;
	byte[] ba_e1 = {1,2,3};
	byte[] ba_e2 = {1,2,3};
	byte[] ba_ne1 = {2,3};
	byte[] ba_ne2 = {1,2,3,4};
	
	@Test(expected = NullPointerException.class)
	public void test_1_exception(){
		ByteArray arr1 = new ByteArray(ba_null);
		System.out.println("Never should be happen: " + arr1);
	}
	
	@Test
	public void test_2_equals_and_not(){
		ByteArray arr1 = new ByteArray(ba_e1);
		ByteArray arr2 = new ByteArray(ba_e2);

		Assert.assertTrue(arr1.equals(arr2));
		Assert.assertTrue(arr2.equals(arr1));
		
		ByteArray arr3 = new ByteArray(ba_ne1);
		ByteArray arr4 = new ByteArray(ba_ne2);
		
		Assert.assertFalse(arr3.equals(null));
		Assert.assertFalse(arr4.equals(arr1));
		Assert.assertFalse(arr3.equals(arr2));
		Assert.assertFalse(arr4.equals(arr2));
	}
}

