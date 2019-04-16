package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A UserDevices
 * 
 * @author Sarath
 * @since Sep 19, 2016
 */

@Entity
@Table(name = "tbl_user_devices")
public class UserDevice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_device_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_device_id") })
	@GeneratedValue(generator = "seq_user_device_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_device_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 500)
	@Column(name = "device_key", length = 500, nullable = false)
	private String deviceKey;

	@NotNull
	@Size(min = 1, max = 500)
	@Column(name = "device_name", length = 500, nullable = false)
	private String deviceName;

	@NotNull
	@Size(min = 1, max = 1000)
	@Column(name = "fcm_key", length = 1000, nullable = false)
	private String fcmKey;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "activated", nullable = false)
	private boolean activated;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();
	
	@Column(name = "last_accessed_login", length = 50, nullable = true)
	private String lastAcessedLogin;
	
	@Column(name = "last_accessed_device_key", length = 500, nullable = true)
	private String lastAcessedDeviceKey;
	
	@Column(name = "build_version", length = 100)
	private String buildVersion;

	public UserDevice() {
		super();
	}

	public UserDevice(String pid, String deviceKey, String deviceName, String fcmKey, User user, Company company,
			boolean activated,String buildVersion) {
		super();
		this.pid = pid;
		this.deviceKey = deviceKey;
		this.deviceName = deviceName;
		this.fcmKey = fcmKey;
		this.user = user;
		this.company = company;
		this.activated = activated;
		this.buildVersion=buildVersion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFcmKey() {
		return fcmKey;
	}

	public void setFcmKey(String fcmKey) {
		this.fcmKey = fcmKey;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLastAcessedLogin() {
		return lastAcessedLogin;
	}

	public void setLastAcessedLogin(String lastAcessedLogin) {
		this.lastAcessedLogin = lastAcessedLogin;
	}

	public String getLastAcessedDeviceKey() {
		return lastAcessedDeviceKey;
	}

	public void setLastAcessedDeviceKey(String lastAcessedDeviceKey) {
		this.lastAcessedDeviceKey = lastAcessedDeviceKey;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	@Override
	public String toString() {
		return "UserDevices [id=" + id + ", pid=" + pid + ", deviceKey=" + deviceKey + ", deviceName=" + deviceName
				+ ", fcmKey=" + fcmKey + ", user=" + user + ", company=" + company + ", activated=" + activated + "]";
	}

}
