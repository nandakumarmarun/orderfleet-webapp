package com.orderfleet.webapp.web.rest.dto;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orderfleet.webapp.domain.model.FirebaseData;

/**
 * A DTO representing a FirebaseRequest
 * 
 * @author Shaheer
 * @since November 03, 2016
 * 
 * @see https://firebase.google.com/docs/cloud-messaging/http-server-ref
 * @see https://firebase.google.com/docs/cloud-messaging/concept-options#notifications_and_data_messages
 */
public class FirebaseRequest {

	@JsonProperty("registration_ids")
	@NotNull
	private String[] registrationIds;
	
	private String priority = "normal";

	private FirebaseData data;

	// This parameter, when set to true, allows developers to test a request
	// without actually sending a message. The default value is false.
	@JsonProperty("dry_run")
	private boolean dryRun = false;

	public String[] getRegistrationIds() {
		return registrationIds;
	}

	public void setRegistrationIds(String[] registrationIds) {
		this.registrationIds = registrationIds;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public FirebaseData getData() {
		return data;
	}

	public void setData(FirebaseData data) {
		this.data = data;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	@Override
	public String toString() {
		return "FirebaseRequest {registrationIds=" + Arrays.toString(registrationIds) + ", data=" + data + "}";
	}

}
