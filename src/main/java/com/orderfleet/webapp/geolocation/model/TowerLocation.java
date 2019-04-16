package com.orderfleet.webapp.geolocation.model;

import java.math.BigDecimal;

public class TowerLocation {

	private String location;

	private BigDecimal lat;

	private BigDecimal lan;

	public TowerLocation() {
	}

	public TowerLocation(String location, BigDecimal lat, BigDecimal lan) {
		super();
		this.location = location;
		this.lat = lat;
		this.lan = lan;
	}

	public String getLocation() {
		return location;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public BigDecimal getLan() {
		return lan;
	}

}
