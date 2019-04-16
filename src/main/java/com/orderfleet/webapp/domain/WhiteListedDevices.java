package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_white_listed_devices")
public class WhiteListedDevices implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_white_listed_devices_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_white_listed_devices_id") })
	@GeneratedValue(generator = "seq_white_listed_devices_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_white_listed_devices_id')")
	private Long id;
	
	@Column(name = "device_name")
	private String deviceName;
	
	@Column(name = "device_key")
	private String deviceKey;
	
	@NotNull
	@Column(name = "device_verification_not_required", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean deviceVerificationNotRequired;

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

	public boolean getDeviceVerificationNotRequired() {
		return deviceVerificationNotRequired;
	}

	public void setDeviceVerificationNotRequired(boolean deviceVerificationNotRequired) {
		this.deviceVerificationNotRequired = deviceVerificationNotRequired;
	}

	@Override
	public String toString() {
		return "WhiteListedDevices [id=" + id + ", deviceName=" + deviceName + ", deviceKey=" + deviceKey
				+ ", deviceVerificationNotRequired=" + deviceVerificationNotRequired + "]";
	}

	
}
