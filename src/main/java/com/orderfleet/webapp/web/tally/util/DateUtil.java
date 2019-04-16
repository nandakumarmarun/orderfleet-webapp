package com.orderfleet.webapp.web.tally.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
	
	private DateUtil(){}
	
	/**
	 * convert string to localdate.
	 *
	 * @param strDate
	 * @return
	 */
	public static LocalDate convertStringToLocalDate(String strDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return LocalDate.parse(strDate, formatter);
	}
	
	public static String convertLocalDateTimeToString(LocalDateTime dateTime,String pattern) {
		
		DateTimeFormatter formatter = null;
		switch (pattern) {
		case "dd-MMM-yyyy": 
			formatter  = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			break;
		case "dd-MM-yyyy":
			formatter  = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			break;
		case "HH:mm:ss a":
			formatter  = DateTimeFormatter.ofPattern("HH:mm:ss a");
			break;
		default :
			formatter  = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			break;
		}
		 
        String formatDateTime = dateTime.format(formatter);
        return formatDateTime;
	}
}
