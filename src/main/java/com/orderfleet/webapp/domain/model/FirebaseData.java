package com.orderfleet.webapp.domain.model;

import java.io.Serializable;

import com.orderfleet.webapp.domain.enums.NotificationMessageType;

/**
 * This is not a normal entity. this is just a dependency for NotificationMessage
 * persisted inside a JSON column.
 * 
 * This is used to save the firebase data object with key value pairs.
 * 
 * @author Shaheer
 * @since October 21, 2016
 * 
 * @see NotificationMessage
 * @see ObjectType
 *
 */
public class FirebaseData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String title;
	
	private String message;
	
	private NotificationMessageType messageType;
	
	private String pidUrl;
	
	private String notificationPid;
	
	private String sentDate;
	
	private boolean todaysPlan;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(NotificationMessageType messageType) {
		this.messageType = messageType;
	}

	public String getPidUrl() {
		return pidUrl;
	}

	public void setPidUrl(String pidUrl) {
		this.pidUrl = pidUrl;
	}

	public String getNotificationPid() {
		return notificationPid;
	}

	public void setNotificationPid(String notificationPid) {
		this.notificationPid = notificationPid;
	}

	public String getSentDate() {
		return sentDate;
	}

	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}

	public boolean getTodaysPlan() {
		return todaysPlan;
	}

	public void setTodaysPlan(boolean todaysPlan) {
		this.todaysPlan = todaysPlan;
	}

	@Override
	public String toString() {
		return "FirebaseData {title=" + title + ", message=" + message + ", messageType=" + messageType + ", pidUrl="
				+ pidUrl + ", notificationPid=" + notificationPid + ", sentDate=" + sentDate + ",todaysPlan="+ todaysPlan + "}";
	}
	
}
