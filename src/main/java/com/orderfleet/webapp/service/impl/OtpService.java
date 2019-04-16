package com.orderfleet.webapp.service.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

@Service
public class OtpService {

	// cache based on username and OPT MAX 8
	private static final Integer EXPIRE_MINS = 5;

	private LoadingCache<String, Integer> otpCache;

	public OtpService() {
		super();
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	// This method is used to push the opt number against Key. Rewrite the OTP if it
	// exists
	// Using user id as key
	public int generateOTP(String key) {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(key, otp);
		return otp;
	}

	// This method is used to return the OPT number against Key->Key values is
	// username
	public int getOtp(String key) {
		try {
			return otpCache.get(key);
		} catch (Exception e) {
			return 0;
		}
	}

	// This method is used to clear the OTP catched already
	public void clearOTP(String key) {
		otpCache.invalidate(key);
	}
	
	public boolean sendOtpMessage(String mobileNumber, int otp) {
		try {
			String authkey = "102303Axh5n6kDaubM5694f339";
			String sender = "salesnrich";
			String Otp = "OTP : " + String.valueOf(otp);
			
			String requestUrl = "http://api.msg91.com/api/sendhttp.php?authkey=" + authkey + "&mobiles=+91" + mobileNumber
					+ "&message=" + URLEncoder.encode(Otp, "UTF-8") + "&sender=" + sender + "&route=4";
			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			System.out.println("ResponseMessage :- " + uc.getResponseMessage());
			System.out.println("ResponseCode :- " + uc.getResponseCode());
			uc.disconnect();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return false;
	}
}
