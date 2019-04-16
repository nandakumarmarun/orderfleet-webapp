package com.orderfleet.webapp.geolocation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleLocation {

	private List<GoogleFormatedAddress> results;

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<GoogleFormatedAddress> getResults() {
		return results;
	}

	public void setResults(List<GoogleFormatedAddress> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "GoogleLocation [results=" + results + ", status=" + status + "]";
	}

}
