package org.hydra.tests.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
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
	//static String pattern_str = "(http|https)://(www\\.|wap\\.)?hydra\\.uz.*";
	static String pattern_str = "^https?://(www\\.|wap\\.)?hydra\\.uz.*";
	static Pattern p = Pattern.compile(pattern_str);
    public static void main(String[] args) throws Exception {
    	Set<String> urls = new HashSet<String>();
		urls.add("http://www.hydra.uz");
		urls.add("https://www.hydra.uz");
		urls.add("http://wap.hydra.uz");
		urls.add("https://wap.hydra.uz");
		urls.add("http://hydra.uz");
		urls.add("https://hydra.uz");
		urls.add("http://some.hydra.uz");    	
		urls.add("https://some.hydra.uz");    	
		
		for(String url:urls){
			if(!testUrl(url)){
				System.out.println(String.format("Error: Pattern(%s): Url: %s", pattern_str, url));
			}else{
				System.out.println(String.format("Ok: Pattern(%s): Url: %s", pattern_str, url));				
			}
		}
    }
	private static boolean testUrl(String url) {
		Matcher matcher = p.matcher(url);
		boolean result = matcher.matches();
		if(result){
			System.out.println("matcher.group(1): " + matcher.group(1));
		}
		return(result);
	}

}