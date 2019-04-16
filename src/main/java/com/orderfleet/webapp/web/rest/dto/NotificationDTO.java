package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.domain.enums.MessageStatus;

/**
 * A DTO for the Notification entity.
 * 
 * @author Sarath
 * @since Sep 7, 2016
 */
public class NotificationDTO {

	private String pid;
	
	private String title;
	
	@NotNull
	private String message;
	private boolean isImportant;
	private Long resendTime;
	private List<String> users;
	private String executiveName;
	private LocalDateTime createdDate;
	private String sendUserName;
	private String sendExecutiveName;
	
	private MessageStatus msgStatus;
	
	private String errorCode;
	
	private String failedReason;
	
	public NotificationDTO() {
		super();
	}
	
	public NotificationDTO(Notification notification) {
		super();
		this.pid = notification.getPid();
		this.message = notification.getMessage();
		this.isImportant = notification.getIsImportant();
		this.resendTime = notification.getResendTime();
		this.createdDate = notification.getCreatedDate();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

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

	public boolean getIsImportant() {
		return isImportant;
	}

	public void setIsImportant(boolean isImportant) {
		this.isImportant = isImportant;
	}

	public Long getResendTime() {
		return resendTime;
	}

	public void setResendTime(Long resendTime) {
		this.resendTime = resendTime;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public String getExecutiveName() {
		return executiveName;
	}

	public void setExecutiveName(String executiveName) {
		this.executiveName = executiveName;
	}

	public String getSendUserName() {
		return sendUserName;
	}

	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}

	public String getSendExecutiveName() {
		return sendExecutiveName;
	}

	public void setSendExecutiveName(String sendExecutiveName) {
		this.sendExecutiveName = sendExecutiveName;
	}

	public MessageStatus getMsgStatus() {
		return msgStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setMsgStatus(MessageStatus msgStatus) {
		this.msgStatus = msgStatus;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	@Override
	public String toString() {
		return "NotificationDTO [pid=" + pid + ", message=" + message + ", isImportant=" + isImportant + ", resendTime="
				+ resendTime + ", users=" + users + ", executiveName=" + executiveName + ", createdDate=" + createdDate
				+ ", sendUserName=" + sendUserName + "]";
	}

}
