package com.orderfleet.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.ApprovalStatus;

/**
 * A DocumentApprovedUser.
 * 
 * @author Muhammed Riyas T
 * @since November 21, 2016
 */
@Entity
@Table(name = "tbl_document_approved_users")
public class DocumentApprovedUser {

	@Id
	@GenericGenerator(name = "seq_document_approved_users_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_approved_users_id") })
	@GeneratedValue(generator = "seq_document_approved_users_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_approved_users_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private DocumentApproval documentApproval;

	@NotNull
	@ManyToOne
	private User user;

	@Column(name = "approval_status", nullable = false)
	private ApprovalStatus approvalStatus;

	@Column(name = "remarks", length = 1500)
	private String remarks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DocumentApproval getDocumentApproval() {
		return documentApproval;
	}

	public void setDocumentApproval(DocumentApproval documentApproval) {
		this.documentApproval = documentApproval;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
