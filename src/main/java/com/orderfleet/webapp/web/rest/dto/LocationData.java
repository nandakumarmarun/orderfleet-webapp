package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;

public class LocationData {

	    private String companyName;

		private String date;

		private BigDecimal latitude;

		private BigDecimal longitude;

		private String key;
	
		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public BigDecimal getLatitude() {
			return latitude;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
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

		@Override
		public String toString() {
			return "LocationData [companyName=" + companyName + ", date=" + date + ", latitude=" + latitude
					+ ", longitude=" + longitude + ", key=" + key + "]";
		}

	}

