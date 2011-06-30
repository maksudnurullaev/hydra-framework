package org.hydra.tests.utils;

import java.util.regex.Pattern;


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
	public static Pattern pattern4Deployer = Pattern.compile("\\[{2}(\\S+?)\\|(\\S+?)\\|(\\S+?)\\|(\\S+?)\\]{2}");
    		
    public static void main(String[] args) throws Exception {
		tests("http://127.0.0.1:8181/?mode=zfile.uz");
		tests("https://127.0.0.1:8181/?mode=zfile.uz");		
		tests("http://127.0.0.1");		
		tests("http://zfile.uz");		
		tests("http://zfile.uz/");		
    }

	private static void tests(String returnFormat) {
		int result = returnFormat.indexOf('/', 8);
		if(result != -1){
			System.out.println(returnFormat.substring(0, result));
		}else{
			System.out.println(returnFormat);
		}
	}

}