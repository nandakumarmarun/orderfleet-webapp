package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.domain.NotificationDetail;
import com.orderfleet.webapp.domain.enums.MessageStatus;

public class NotificationDetailDto {

	private Long id;

	private NotificationDTO notification;

	private Long userDeviceId;

	private MessageStatus messageStatus = MessageStatus.NONE;

	private String errorCode;

	private String failedReason;

	private String fcmKey;

	private Long userId;

	private String userName;

	private String userPid;

	private long companyId;

	private String createdDate;

	List<NotificationReplyDTO> notificationReplyDtoList;

	public NotificationDetailDto(NotificationDetail notificationDetail) {
		super();
		this.id = notificationDetail.getId();
		this.notification = new NotificationDTO(notificationDetail.getNotification());
		this.userDeviceId = notificationDetail.getUserDeviceId();
		this.messageStatus = notificationDetail.getMessageStatus();
		this.errorCode = notificationDetail.getErrorCode();
		this.failedReason = notificationDetail.getFailedReason();
		this.fcmKey = notificationDetail.getFcmKey();
		this.userId = notificationDetail.getUserId();
		this.userPid = notificationDetail.getUserPid();
		this.companyId = notificationDetail.getCompany().getId();
		this.createdDate = notificationDetail.getCreatedDate().toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationDTO getNotification() {
		return notification;
	}

	public void setNotification(NotificationDTO notification) {
		this.notification = notification;
	}

	public Long getUserDeviceId() {
		return userDeviceId;
	}

	public void setUserDeviceId(Long userDeviceId) {
		this.userDeviceId = userDeviceId;
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
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

	public void setFcmKey(String fcmKey) {
		this.fcmKey = fcmKey;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public List<NotificationReplyDTO> getNotificationReplyDtoList() {
		return notificationReplyDtoList;
	}

	public void setNotificationReplyDtoList(List<NotificationReplyDTO> notificationReplyDtoList) {
		this.notificationReplyDtoList = notificationReplyDtoList;
	}

	@Override
	public String toString() {
		return "NotificationDetailDto [id=" + id + ", notification=" + notification + ", userDeviceId=" + userDeviceId
				+ ", messageStatus=" + messageStatus + ", errorCode=" + errorCode + ", failedReason=" + failedReason
				+ ", fcmKey=" + fcmKey + ", userId=" + userId + ", userPid=" + userPid + ", companyId=" + companyId
				+ ", createdDate=" + createdDate + "]";
	}

}
