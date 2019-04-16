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
public class ActivityNotificationDTO {

	private long id;

	private NotificationType notificationType;

	private String activityPid;

	private String activityName;

	private String documentName;

	private String documentPid;

	private boolean sendCustomer;

	private boolean other;

	private String phoneNumbers;

	public ActivityNotificationDTO() {
		super();
	}

	public ActivityNotificationDTO(ActivityNotification activityNotification) {
		super();
		this.id = activityNotification.getId();
		this.notificationType = activityNotification.getNotificationType();
		this.activityPid = activityNotification.getActivity().getPid();
		this.activityName = activityNotification.getActivity().getName();
		this.documentName = activityNotification.getDocument().getName();
		this.documentPid = activityNotification.getDocument().getPid();
		this.sendCustomer = activityNotification.getSendCustomer();
		this.other = activityNotification.getOther();
		this.phoneNumbers = activityNotification.getPhoneNumbers();
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public boolean getSendCustomer() {
		return sendCustomer;
	}

	public void setSendCustomer(boolean sendCustomer) {
		this.sendCustomer = sendCustomer;
	}

	public boolean getOther() {
		return other;
	}

	public void setOther(boolean other) {
		this.other = other;
	}

	public String getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(String phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	@Override
	public String toString() {
		return "ActivityNotificationDTO [id=" + id + ", notificationType=" + notificationType + ", activityPid="
				+ activityPid + ", activityName=" + activityName + ", documentName=" + documentName + ", documentPid="
				+ documentPid + "]";
	}
}
