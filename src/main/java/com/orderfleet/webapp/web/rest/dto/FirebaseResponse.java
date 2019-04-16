package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A DTO representing a FirebaseResponse
 * 
 * @author Shaheer
 * @since November 03, 2016
 * 
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref#interpret-downstream
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref#error-codes
 */
public class FirebaseResponse {

	@JsonProperty("multicast_id")
	private String multicastId;

	private int success;

	private int failure;

	@JsonProperty("canonical_ids")
	private int deviceIds;

	private List<FirebaseResponseResult> results;

	public String getMulticastId() {
		return multicastId;
	}

	public void setMulticastId(String multicastId) {
		this.multicastId = multicastId;
	}

	/**
	 * Number of messages that were processed without an error.
	 */
	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	/**
	 * Number of messages that could not be processed.
	 */
	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	/**
	 * A canonical registration ID is the registration token of the last
	 * registration requested by the client app. This is the ID that the server
	 * should use when sending messages to the device
	 */
	public int getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(int deviceIds) {
		this.deviceIds = deviceIds;
	}

	/**
	 * Array of objects representing the status of the messages processed.
	 */
	public List<FirebaseResponseResult> getResults() {
		return results;
	}

	public void setResults(List<FirebaseResponseResult> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "FirebaseResponse {multicastId=" + multicastId + ", success=" + success + ", failure=" + failure
				+ ", deviceIds=" + deviceIds + ", results=" + results + "}";
	}
	
}
