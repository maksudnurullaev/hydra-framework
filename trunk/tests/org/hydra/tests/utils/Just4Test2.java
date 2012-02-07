package org.hydra.tests.utils;
import java.net.*;

public class Just4Test2 {

	 public static void main(String [] args) {
	 try {
		  InetAddress addr = InetAddress.getLocalHost();
		  String hostname = addr.getHostName();
		  System.out.println("hostname="+hostname);
		  } catch (UnknownHostException e) {
			  System.out.println(e.getMessage());
		  }
	  }
}
