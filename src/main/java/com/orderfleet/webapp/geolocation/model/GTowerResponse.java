package com.orderfleet.webapp.geolocation.model;

public class GTowerResponse {

	private GTowerLatLan location;

	private long accuracy;

	public GTowerLatLan getLocation() {
		return location;
	}

	public void setLocation(GTowerLatLan location) {
		this.location = location;
	}

	public long getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(long accuracy) {
		this.accuracy = accuracy;
	}

}
