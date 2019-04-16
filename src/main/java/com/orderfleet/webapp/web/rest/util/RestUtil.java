package com.orderfleet.webapp.web.rest.util;

import org.springframework.http.HttpStatus;

public class RestUtil {
	
	private RestUtil() {}
	
	public static boolean isError(HttpStatus status) {
		HttpStatus.Series series = status.series();
		return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
	}
}
