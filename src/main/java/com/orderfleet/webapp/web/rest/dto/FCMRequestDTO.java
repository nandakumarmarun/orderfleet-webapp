package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A DTO representing a FCM Request
 * 
 * @author Shaheer
 * @since September 09, 2016
 * 
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref
 */
public class FCMRequestDTO {
	
	private Notification notification;
	
	@NotNull
	private String[] registrationIdsToSend;

	@JsonProperty("registration_ids")
	public String[] getRegistrationIdsToSend() {
		return registrationIdsToSend;
	}

	public void setRegistrationIdsToSend(String[] registrationIdsToSend) {
		this.registrationIdsToSend = registrationIdsToSend;
	}
	
	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public class Notification {
		
		private String title;
		
		@NotNull
		private String message;
		
		@JsonProperty("title")
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		@JsonProperty("body")
		public String getMessage() {
			return message;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}
	}
	
}

