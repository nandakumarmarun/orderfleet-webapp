package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.NotificationType;

@Entity
@Table(name = "tbl_activity_notification")
public class ActivityNotification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_activity_notification_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_activity_notification_id") })
	@GeneratedValue(generator = "seq_activity_notification_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_activity_notification_id')")
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", nullable = false)
	private NotificationType notificationType;

	@NotNull
	@ManyToOne
	private Activity activity;

	@ManyToOne
	private Document document;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "send_customer", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean sendCustomer = true;

	@NotNull
	@Column(name = "other", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean other = false;

	@Column(name = "phone_numbers")
	private String phoneNumbers;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ActivityNotification activityNotification = (ActivityNotification) o;
		if (activityNotification.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, activityNotification.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}