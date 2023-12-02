package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;

public class LocationData {

	    private String companyName;

		private String currentDateTime;

		private BigDecimal latitude;

		private BigDecimal longitude;

		private String key;
	
		private Integer battery_percentage;
		private String Address;
		
		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public BigDecimal getLatitude() {
			return latitude;
		}


	public String getCurrentDateTime() {
		return currentDateTime;
	}

	public void setCurrentDateTime(String currentDateTime) {
		this.currentDateTime = currentDateTime;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public void setLatitude(BigDecimal latitude) {
			this.latitude = latitude;
		}

		public BigDecimal getLongitude() {
			return longitude;
		}

		public void setLongitude(BigDecimal longitude) {
			this.longitude = longitude;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}


	public Integer getBattery_percentage() {
		return battery_percentage;
	}

	public void setBattery_percentage(Integer battery_percentage) {
		this.battery_percentage = battery_percentage;
	}


	@Override
	public String toString() {
		return "LocationData{" +
				"companyName='" + companyName + '\'' +
				", currentDate='" + currentDateTime + '\'' +
				", latitude=" + latitude +
				", longitude=" + longitude +
				", key='" + key + '\'' +
				", battery_percentage=" + battery_percentage +
				", Address='" + Address + '\'' +
				'}';
	}
}


