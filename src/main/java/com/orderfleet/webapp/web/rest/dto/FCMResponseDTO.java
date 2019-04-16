package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A DTO representing a FCM Response
 * 
 * @author Shaheer
 * @since September 09, 2016
 * 
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref
 */
public class FCMResponseDTO {
	
	@JsonProperty("multicast_id")
	private String multicastId;
	
	private int success;

	private int failure;
	
	@JsonProperty("canonical_ids")
	private int canonicalIds;
	
	private Results results;

	public String getMulticastId() {
		return multicastId;
	}

	public void setMulticastId(String multicastId) {
		this.multicastId = multicastId;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public int getCanonicalIds() {
		return canonicalIds;
	}

	public void setCanonicalIds(int canonicalIds) {
		this.canonicalIds = canonicalIds;
	}

	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}

	public class Results {
		
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
		
	}
	
}

