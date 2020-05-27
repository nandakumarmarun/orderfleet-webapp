package com.orderfleet.webapp.web.util;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public final class RestClientUtil {
	
	private static String AUTH_KEY;
	
	private RestClientUtil() {}
	
	public static MultiValueMap<String, String> createTokenAuthHeaders() {
		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
		//requestHeaders.add("Authorization", "Bearer " + RestClientUtil.AUTH_KEY);
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return requestHeaders;
    }

	public static void setAuthKey(String authKey) {
		RestClientUtil.AUTH_KEY = authKey;
	}

}
