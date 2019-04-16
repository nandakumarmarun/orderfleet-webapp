package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.WhiteListedDevices;

public class WhiteListedDevicesDTO {
	
	private Long id;
	
	private String deviceName;
	
	private String deviceKey;
	
	private boolean deviceVerificationNotRequired;

	public WhiteListedDevicesDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WhiteListedDevicesDTO(WhiteListedDevices whiteListedDevices) {
		super();
		this.id = whiteListedDevices.getId();
		this.deviceName = whiteListedDevices.getDeviceName();
		this.deviceKey = whiteListedDevices.getDeviceKey();
		this.deviceVerificationNotRequired = whiteListedDevices.getDeviceVerificationNotRequired();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public boolean isDeviceVerificationNotRequired() {
		return deviceVerificationNotRequired;
	}

	public void setDeviceVerificationNotRequired(boolean deviceVerificationNotRequired) {
		this.deviceVerificationNotRequired = deviceVerificationNotRequired;
	}
	
	
}
