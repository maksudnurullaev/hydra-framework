package org.hydra.tests.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;


public class Main {
		final static String[] days_of_week = {"", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
		
        public static void main(String[] args){
            Scanner in = new Scanner(System.in);
            List<String> strings = new ArrayList<String>();
            
            String line = in.nextLine();
            while(line.matches("^\\d{4}-\\d{2}$")){
            	strings.add(line);
                line = in.nextLine();
            }
            
            for(String string: strings){
            	printDayOf28(string);
            }
       }

		private static void printDayOf28(String string) {
			String[] date_parts = string.split("-");

				Calendar xmas = new GregorianCalendar(
						Integer.valueOf(date_parts[0]), 
						Integer.valueOf(date_parts[1]) -1, 
						28);
				
				System.out.println(days_of_week[xmas.get(Calendar.DAY_OF_WEEK)]);
		}
}