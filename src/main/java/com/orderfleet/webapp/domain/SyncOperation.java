package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

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

import com.orderfleet.webapp.domain.enums.SyncOperationType;

/**
 * A SyncOperation
 *
 * @author sarath
 * @since March 13, 2017
 */
@Entity
@Table(name = "tbl_sync_operation")
public class SyncOperation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sync_operation_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sync_operation_id") })
	@GeneratedValue(generator = "seq_sync_operation_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sync_operation_id')")
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "operation_type", nullable = false)
	private SyncOperationType operationType;

	private LocalDateTime lastSyncStartedDate;

	private LocalDateTime lastSyncCompletedDate;

	// total synchronization time (in milliseconds).
	private double lastSyncTime;

	@NotNull
	@Column(name = "completed", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean completed = false;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "is_user", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean user;

	@NotNull
	@Column(name = "is_document", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean document;

	@NotNull
	@Column(name = "reset", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean reset;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SyncOperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(SyncOperationType operationType) {
		this.operationType = operationType;
	}

	public LocalDateTime getLastSyncStartedDate() {
		return lastSyncStartedDate;
	}

	public void setLastSyncStartedDate(LocalDateTime lastSyncStartedDate) {
		this.lastSyncStartedDate = lastSyncStartedDate;
	}

	public LocalDateTime getLastSyncCompletedDate() {
		return lastSyncCompletedDate;
	}

	public void setLastSyncCompletedDate(LocalDateTime lastSyncCompletedDate) {
		this.lastSyncCompletedDate = lastSyncCompletedDate;
	}

	public double getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(double lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public boolean getCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean getUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public boolean getDocument() {
		return document;
	}

	public void setDocument(boolean document) {
		this.document = document;
	}

	public boolean getReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

}
