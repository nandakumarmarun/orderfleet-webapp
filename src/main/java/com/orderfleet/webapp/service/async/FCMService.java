package com.orderfleet.webapp.service.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.orderfleet.webapp.web.rest.dto.FCMRequestDTO;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;

/**
 * Service for sending push notifications to android devices.
 * <p>
 * We use the @Async annotation to send notifications asynchronously.
 * </p>
 * 
 * @author Shaheer
 * @since September 08, 2016
 */
@Service
public class FCMService {

	private final Logger log = LoggerFactory.getLogger(FCMService.class);

	private static final String AUTH_KEY_FCM = "AIzaSyB_KOvF4OXz0C6gM7kLE8BrIhgBjs2QLsg";
	private static final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	// Method to send Notifications from server to client end.
	// @Async
	// TODO: Make this async...
	public FirebaseResponse pushFCMNotification(FCMRequestDTO notification) {
		log.debug("Send push notification to '{}' with and content={}", notification.getRegistrationIdsToSend(),
				notification.getNotification().getMessage());

		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
		requestHeaders.add("Authorization", "Key=" + AUTH_KEY_FCM);
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the network request
		ResponseEntity<FirebaseResponse> response = restTemplate.exchange(API_URL_FCM, HttpMethod.POST,
				new HttpEntity<FCMRequestDTO>(notification, requestHeaders), FirebaseResponse.class);
		return response.getBody();

	}
}
