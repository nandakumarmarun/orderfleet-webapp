package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A Notification
 * 
 * @author Sarath
 * @since Sep 7, 2016
 */
@Entity
@Table(name = "tbl_notification")
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_notification_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_notification_id") })
	@GeneratedValue(generator = "seq_notification_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_notification_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@Column(name = "title")
	private String title;

	@Column(name = "message" ,length = 1024)
	private String message;

	@Column(name = "is_importent")
	private boolean isImportant;

	@Column(name = "resend_time")
	private Long resendTime;
	
	@Column(name = "success", nullable = false, columnDefinition = "int DEFAULT 0")
	private int success;

	@Column(name = "failure", nullable = false, columnDefinition = "int DEFAULT 0")
	private int failure;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@ManyToOne
	private User createdBy;
	
	@ManyToOne
	@NotNull
	private Company company;
	
	@Transient
	private List<NotificationDetail> notificationDetails = new ArrayList<>();

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

	public void setisImportant(boolean isImportant) {
		this.isImportant = isImportant;
	}

	public Long getResendTime() {
		return resendTime;
	}

	public void setResendTime(Long resendTime) {
		this.resendTime = resendTime;
	}

	public int getSuccess() {
		return success;
	}

	public int getFailure() {
		return failure;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public List<NotificationDetail> getNotificationDetails() {
		return notificationDetails;
	}

	public void setNotificationDetails(List<NotificationDetail> notificationDetails) {
		this.notificationDetails = notificationDetails;
	}

	
	
}
