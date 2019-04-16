package com.orderfleet.webapp.geolocation.model;

public class CellTower {

	private String cellId;

	private String locationAreaCode;

	private String mobileCountryCode;

	private String mobileNetworkCode;

	public CellTower(String cellId, String locationAreaCode, String mobileCountryCode, String mobileNetworkCode) {
		super();
		this.cellId = cellId;
		this.locationAreaCode = locationAreaCode;
		this.mobileCountryCode = mobileCountryCode;
		this.mobileNetworkCode = mobileNetworkCode;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getLocationAreaCode() {
		return locationAreaCode;
	}

	public void setLocationAreaCode(String locationAreaCode) {
		this.locationAreaCode = locationAreaCode;
	}

	public String getMobileCountryCode() {
		return mobileCountryCode;
	}

	public void setMobileCountryCode(String mobileCountryCode) {
		this.mobileCountryCode = mobileCountryCode;
	}

	public String getMobileNetworkCode() {
		return mobileNetworkCode;
	}

	public void setMobileNetworkCode(String mobileNetworkCode) {
		this.mobileNetworkCode = mobileNetworkCode;
	}

}
