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

	public ChatReplyNotificationDTO() {
		super();
	}

//	public ActivityNotificationDTO(long id, NotificationType notificationType, String activityPid, String activityName,
//			String documentName, String documentPid, boolean sendCustomer, boolean other, String phoneNumbers) {
//		super();
//		this.id = id;
//		this.notificationType = notificationType;
//		this.activityPid = activityPid;
//		this.activityName = activityName;
//		this.documentName = documentName;
//		this.documentPid = documentPid;
//		this.sendCustomer = sendCustomer;
//		this.other = other;
//		this.phoneNumbers = phoneNumbers;
//	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
