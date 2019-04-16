package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.MessageStatus;

public class NotificationMessageRecipientDTO {

	private String userName;
	
	private MessageStatus messageStatus;
	
	

	public NotificationMessageRecipientDTO(String userName, MessageStatus messageStatus) {
		super();
		this.userName = userName;
		this.messageStatus = messageStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}
	
	
}
