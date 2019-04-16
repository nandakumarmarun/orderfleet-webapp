package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * @author Sarath
 * @since Sep 3, 2016
 */

/**
 * A UserReceiptTargetDocument.
 */
@Entity
@Table(name = "tbl_user_receipt_target_document")
public class UserReceiptTargetDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_receipt_target_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_receipt_target_document_id") })
	@GeneratedValue(generator = "seq_user_receipt_target_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_receipt_target_document_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@NotNull
	private UserReceiptTarget userReceiptTarget;

	@NotNull
	@ManyToOne
	private Document document;

	@ManyToOne
	@NotNull
	private Company company;

	public UserReceiptTargetDocument() {
		super();
	}

	public UserReceiptTargetDocument(String pid, UserReceiptTarget userReceiptTarget, Document document, Company company) {
		super();
		this.pid = pid;
		this.userReceiptTarget = userReceiptTarget;
		this.document = document;
		this.company = company;
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

	public UserReceiptTarget getUserReceiptTarget() {
		return userReceiptTarget;
	}

	public void setUserReceiptTarget(UserReceiptTarget userReceiptTarget) {
		this.userReceiptTarget = userReceiptTarget;
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

	@Override
	public String toString() {
		return "UserReceiptTargetDocument [id=" + id + ", pid=" + pid + ", userReceiptTarget="
				+ userReceiptTarget.getTargetAmount() + ", document=" + document.getName() + ", company="
				+ company.getLegalName() + "]";
	}

}
