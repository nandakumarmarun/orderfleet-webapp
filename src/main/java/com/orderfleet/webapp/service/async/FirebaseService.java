package com.orderfleet.webapp.service.async;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.orderfleet.webapp.domain.NotificationMessage;
import com.orderfleet.webapp.domain.NotificationMessageRecipient;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.enums.MessageStatus;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.NotificationMessageRepository;
import com.orderfleet.webapp.service.NotificationMessageService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;

/**
 * Service for sending push notifications to android devices.
 * <p>
 * We use the @Async annotation to send notifications asynchronously.
 * </p>
 * 
 * @see https://firebase.google.com/docs/cloud-messaging/send-message#http_post_request
 * 
 * @author Shaheer
 * @since November 02, 2016
 */
@Service
public class FirebaseService {

	private final Logger log = LoggerFactory.getLogger(FirebaseService.class);

	private static final String AUTH_KEY = "AIzaSyB_KOvF4OXz0C6gM7kLE8BrIhgBjs2QLsg";
//	private static final String AUTH_KEY = "AAAAeVBzrLY:APA91bHTKgxvkBi-KqGYigSO-MBtRlIMercMOVH1h5DuIDcSFzVlclt6_Eskh7n-DJKuKMceLQfBstMPRoDvLij9kd0tiQRxWm09HMLaUyAYNsa60CtmhK6JJawwfkO-ok2uuQ_ZAXLQ"; // Modern
	private static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";

	private NotificationMessageRepository notificationMessageRepository;

	@Inject
	public FirebaseService(NotificationMessageRepository notificationMessageRepository) {
		super();
		this.notificationMessageRepository = notificationMessageRepository;
	}

	@Async
	public NotificationMessage sendNotificationToUsers(FirebaseRequest firebaseRequest, List<UserDevice> userDevices,
			String createdBy) {
		log.debug("Send notifications to '{}' with content={}", firebaseRequest.getRegistrationIds(),
				firebaseRequest.getData());
		String notificationPid = NotificationMessageService.PID_PREFIX + RandomUtil.generatePid();
		firebaseRequest.getData().setNotificationPid(notificationPid);
		try {
			MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
			requestHeaders.add("Authorization", "Key=" + AUTH_KEY);
			requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			// Make the network request
			ResponseEntity<FirebaseResponse> response = restTemplate.exchange(FIREBASE_URL, HttpMethod.POST,
					new HttpEntity<FirebaseRequest>(firebaseRequest, requestHeaders), FirebaseResponse.class);
			log.debug("Successfully pushed notifications to devices'{}'",
					Arrays.toString(firebaseRequest.getRegistrationIds()));
			return saveToDB(notificationPid, response.getBody(), firebaseRequest.getData(), userDevices, createdBy);
		} catch (Exception e) {
			log.warn("Notification could not be sent to devices '{}'",
					Arrays.toString(firebaseRequest.getRegistrationIds()), e);
		}
		return null;
	}

	public FirebaseResponse sendSynchronousNotificationToUsers(FirebaseRequest firebaseRequest, String createdBy) {
		log.debug("Send notifications to '{}' with content={}", firebaseRequest.getRegistrationIds(),
				firebaseRequest.getData());
		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
		requestHeaders.add("Authorization", "Key=" + AUTH_KEY);
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		// Make the network request
		ResponseEntity<FirebaseResponse> response = restTemplate.exchange(FIREBASE_URL, HttpMethod.POST,
				new HttpEntity<FirebaseRequest>(firebaseRequest, requestHeaders), FirebaseResponse.class);
		log.debug("Successfully pushed notifications to devices'{}'",
				Arrays.toString(firebaseRequest.getRegistrationIds()));
		return response.getBody();
	}

	private NotificationMessage saveToDB(String notificationPid, FirebaseResponse firebaseResponse,
			FirebaseData firebaseData, List<UserDevice> userDevices, String createdBy) {
		log.debug("Firebase response => {} , Success :- {} Failed :- {}, ", firebaseResponse,
				firebaseResponse.getSuccess(), firebaseResponse.getFailure());
		log.debug("Saving to database.");

		NotificationMessage notificationMessage = new NotificationMessage();
		notificationMessage.setPid(notificationPid);
		notificationMessage.setCreatedBy(createdBy);
		notificationMessage.setMessageData(firebaseData);
		notificationMessage.setNotificationType(firebaseData.getMessageType());
		notificationMessage.setExpiryDate(LocalDateTime.now().plusMonths(1));
		notificationMessage.setCompany(userDevices.get(0).getCompany());

		Set<NotificationMessageRecipient> messageRecipients = new HashSet<>();
		for (UserDevice userDevice : userDevices) {
			NotificationMessageRecipient recipient = new NotificationMessageRecipient();
			recipient.setNotificationMessage(notificationMessage);
			recipient.setUserDevice(userDevice);
			recipient.setMessageStatus(MessageStatus.SENT);
			recipient.setCompany(userDevice.getCompany());
			notificationMessage.setMessageRecipients(messageRecipients);
			messageRecipients.add(recipient);
		}
		notificationMessage.setMessageRecipients(messageRecipients);
		notificationMessage = notificationMessageRepository.save(notificationMessage);
		return notificationMessage;
	}

}