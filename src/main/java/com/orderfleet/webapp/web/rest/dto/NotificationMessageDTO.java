package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.enums.NotificationMessageType;

public class NotificationMessageDTO {

	private String pid;
	private String createdBy;
	private String messageData;
	private LocalDateTime createdDate; 
	private NotificationMessageType notificationType;
	private LocalDateTime expiryDate;
	private boolean remind = false;
	private LocalDateTime nextRemindDate;
	private List<NotificationMessageRecipientDTO> notificationReceipts;
	
	
	public NotificationMessageDTO() {
	
	}


	public NotificationMessageDTO(String pid, String createdBy, String messageData, LocalDateTime createdDate,
			NotificationMessageType notificationType, LocalDateTime expiryDate, boolean remind,
			LocalDateTime nextRemindDate, List<NotificationMessageRecipientDTO> notificationReceipts) {
		super();
		this.pid = pid;
		this.createdBy = createdBy;
		this.messageData = messageData;
		this.createdDate = createdDate;
		this.notificationType = notificationType;
		this.expiryDate = expiryDate;
		this.remind = remind;
		this.nextRemindDate = nextRemindDate;
		this.notificationReceipts = notificationReceipts;
	}


	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public String getMessageData() {
		return messageData;
	}


	public void setMessageData(String messageData) {
		this.messageData = messageData;
	}


	public LocalDateTime getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}


	public NotificationMessageType getNotificationType() {
		return notificationType;
	}


	public void setNotificationType(NotificationMessageType notificationType) {
		this.notificationType = notificationType;
	}


	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}


	public boolean isRemind() {
		return remind;
	}


	public void setRemind(boolean remind) {
		this.remind = remind;
	}


	public LocalDateTime getNextRemindDate() {
		return nextRemindDate;
	}


	public void setNextRemindDate(LocalDateTime nextRemindDate) {
		this.nextRemindDate = nextRemindDate;
	}


	public List<NotificationMessageRecipientDTO> getNotificationReceipts() {
		return notificationReceipts;
	}


	public void setNotificationReceipts(List<NotificationMessageRecipientDTO> notificationReceipts) {
		this.notificationReceipts = notificationReceipts;
	}


	@Override
	public String toString() {
		return "NotificationMessageDTO [pid=" + pid + ", createdBy=" + createdBy + ", messageData=" + messageData
				+ ", createdDate=" + createdDate + ", notificationType=" + notificationType + ", expiryDate="
				+ expiryDate + ", remind=" + remind + ", nextRemindDate=" + nextRemindDate + ", notificationReceipts="
				+ notificationReceipts + "]";
	}


	



	


	

	
	
	
	
	
}
