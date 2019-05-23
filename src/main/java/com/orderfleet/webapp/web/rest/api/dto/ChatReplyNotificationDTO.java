package com.orderfleet.webapp.web.rest.api.dto;

import com.orderfleet.webapp.domain.ActivityNotification;
import com.orderfleet.webapp.domain.enums.NotificationType;

/**
 * A DTO For ActivityNotification Entity.
 *
 * @author Sarath
 * @since May 4, 2017
 *
 */
public class ChatReplyNotificationDTO {

	private String message;
	
	private String notificationPid;

	public ChatReplyNotificationDTO() {
		super();
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
	
	
}
