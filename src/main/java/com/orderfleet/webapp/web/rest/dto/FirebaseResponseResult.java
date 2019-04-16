package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representing the status of the messages processed.
 * 
 * @author Shaheer
 * @since November 03, 2016
 * 
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref#interpret-downstream
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref#error-codes
 * @see https://developers.google.com/cloud-messaging/http
 */
public class FirebaseResponseResult {

	@JsonProperty("message_id")
	private String messageId;

	@JsonProperty("registration_id")
	private String registrationId;

	@JsonProperty("error")
	private String error;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "{messageId=" + messageId + ", registrationId=" + registrationId + ", error="
				+ error + "}";
	}

}
