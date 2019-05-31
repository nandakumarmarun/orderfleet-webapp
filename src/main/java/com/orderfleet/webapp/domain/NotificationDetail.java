package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.MessageStatus;

@Entity
@Table(name = "tbl_notification_details")
public class NotificationDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_notification_details_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_notification_details_id") })
	@GeneratedValue(generator = "seq_notification_details_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_notification_details_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Notification notification;
	
	private Long userDeviceId;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "message_status", nullable = false,columnDefinition = "varchar(255) DEFAULT 'NONE'")
	private MessageStatus messageStatus = MessageStatus.NONE;
	
	@Column(name = "error_code")
	private String errorCode;
	
	@Column(name = "failed_reason", length = 5000)
	private String failedReason;
	
	@NotNull
	@Size(min = 1, max = 1000)
	@Column(name = "fcm_key", length = 1000, nullable = false)
	private String fcmKey;

	private Long userId;
	
	private String userPid;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public Notification getNotification() {
		return notification;
	}

	public Long getUserDeviceId() {
		return userDeviceId;
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public String getFcmKey() {
		return fcmKey;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserPid() {
		return userPid;
	}

	public Company getCompany() {
		return company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public void setUserDeviceId(Long userDeviceId) {
		this.userDeviceId = userDeviceId;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}

	public void setFcmKey(String fcmKey) {
		this.fcmKey = fcmKey;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	
	
}
