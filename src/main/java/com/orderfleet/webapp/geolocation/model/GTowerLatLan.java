package com.orderfleet.webapp.geolocation.model;

import java.math.BigDecimal;

public class GTowerLatLan {

	private BigDecimal lat;
	private BigDecimal lng;

	public BigDecimal getLng() {
		return lng;
	}

	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

}