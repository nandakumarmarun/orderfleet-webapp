package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.MessageStatus;

/**
 * A NotificationMessageRecipient
 * 
 * Sending messages to multiple recipients at once.
 * 
 * @author Shaheer
 * @since November 02, 2016
 */
@Entity
@Table(name = "tbl_notification_message_recipient")
public class NotificationMessageRecipient implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_notification_message_recipient_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_notification_message_recipient_id") })
	@GeneratedValue(generator = "seq_notification_message_recipient_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_notification_message_recipient_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private NotificationMessage notificationMessage;

	@ManyToOne
	@NotNull
	private UserDevice userDevice;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_status")
	private MessageStatus messageStatus = MessageStatus.SENT;

	@Column(name = "read_date", nullable = true)
	private LocalDateTime readDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationMessage getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(NotificationMessage notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public UserDevice getUserDevice() {
		return userDevice;
	}

	public void setUserDevice(UserDevice userDevice) {
		this.userDevice = userDevice;
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}

	public LocalDateTime getReadDate() {
		return readDate;
	}

	public void setReadDate(LocalDateTime readDate) {
		this.readDate = readDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
