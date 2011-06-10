package org.hydra.tests.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.hydra.utils.Utils;

public class Just4Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(RandomStringUtils.random(8,"poiuytrewwqlkjhgfdsmnbvcxz"));			
			System.out.println(RandomStringUtils.random(8,true, true));			
			System.out.println(Utils.GetUUID());			
		}
	}

}
