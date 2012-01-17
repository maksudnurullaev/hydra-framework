package org.hydra.tests.utils;

import org.hydra.utils.FileUtils;

/**
* A simple example showing what it takes to page over results using
* get_range_slices.
*
* To run this example from maven:
* mvn -e exec:java -Dexec.mainClass="com.riptano.cassandra.hector.example.PaginateGetRangeSlices"
*
* @author zznate
*
*/
public class Just4Run {
    public static void main(String[] args) throws Exception {
    	String name1 = "test/dfasfas\\1.txt";
    	String name2 = "test/dfasfas/fsdfas/22.txt";
    	String name3 = "test\\dfasfas\\333.txt";
    	String name4 = "4444.txt";
    	System.out.println(FileUtils.sanitize(name1));
    	System.out.println(FileUtils.sanitize(name2));
    	System.out.println(FileUtils.sanitize(name3));
    	System.out.println(FileUtils.sanitize(name4));
    }

}