package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;

/**
 * A NotificationMessage
 * 
 * @author Shaheer
 * @since November 02, 2016
 */
@Entity
@Table(name = "tbl_notification_message")
public class NotificationMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_notification_message_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_notification_message_id") })
	@GeneratedValue(generator = "seq_notification_message_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_notification_message_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@CreatedBy
	@Column(name = "created_by", nullable = false, length = 50, updatable = false)
	@JsonIgnore
	private String createdBy;

	@CreatedDate
	@Column(name = "created_date", nullable = false)
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "message_data")
	@Type(type = "com.orderfleet.webapp.domain.usertype.ObjectType", parameters = {
			@org.hibernate.annotations.Parameter(name = "type", value = "classType"),
			@org.hibernate.annotations.Parameter(name = "element", value = "com.orderfleet.webapp.domain.model.FirebaseData") })
	private FirebaseData messageData;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", nullable = false)
	private NotificationMessageType notificationType;

	// for now set it as one month
	@NotNull
	@Column(name = "expiry_date", nullable = false)
	private LocalDateTime expiryDate;

	// This column flags whether or not a reminder is required for the message.
	// Now not implemented so it is always false.
	@Column(name = "remind")
	private boolean remind = false;

	@Column(name = "next_remind_date", nullable = true)
	private LocalDateTime nextRemindDate;

	// @ManyToOne
	// private ReminderFrequency reminderFrequency;

	@ManyToOne
	@NotNull
	private Company company;

	@OneToMany(mappedBy = "notificationMessage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<NotificationMessageRecipient> messageRecipients;

	public NotificationMessage() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public FirebaseData getMessageData() {
		return messageData;
	}

	public void setMessageData(FirebaseData messageData) {
		this.messageData = messageData;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<NotificationMessageRecipient> getMessageRecipients() {
		return messageRecipients;
	}

	public void setMessageRecipients(Set<NotificationMessageRecipient> messageRecipients) {
		this.messageRecipients = messageRecipients;
	}

	@Override
	public String toString() {
		return "NotificationMessage [pid=" + pid + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", messageData=" + messageData + ", notificationType=" + notificationType + ", expiryDate="
				+ expiryDate + ", remind=" + remind + ", nextRemindDate=" + nextRemindDate + "]";
	}

}
