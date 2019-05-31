package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.NotificationReply;

public class NotificationReplyDTO {

	private String message;
	
	private String notificationPid;
	
	private LocalDateTime createdDate;
	

	public NotificationReplyDTO() {
		super();
	}
	
	

	public NotificationReplyDTO(NotificationReply notificationReply) {
		super();
		this.message = notificationReply.getReplyMessage();
		this.notificationPid = notificationReply.getNotification().getPid();
		this.createdDate = notificationReply.getCreatedDate();
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNotificationPid() {
		return notificationPid;
	}

	public void setNotificationPid(String notificationPid) {
		this.notificationPid = notificationPid;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	
}
