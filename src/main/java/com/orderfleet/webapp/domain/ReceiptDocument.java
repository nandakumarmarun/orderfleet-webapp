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
 * A ReceiptDocument
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */

@Entity
@Table(name = "tbl_receipt_document")
public class ReceiptDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_receipt_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_receipt_document_id") })
	@GeneratedValue(generator = "seq_receipt_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_receipt_document_id')")
	private Long id;

	@ManyToOne()
	@NotNull
	private Document document;

	@ManyToOne()
	@NotNull
	private Company company;

	@Column(name = "is_activated", nullable = false)
	private boolean activated = true;

	public ReceiptDocument() {
		super();
	}

	public ReceiptDocument(Document document, Company company, boolean activated) {
		super();
		this.document = document;
		this.company = company;
		this.activated = activated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "ReceiptDocument [id=" + id + ", document=" + document + ", company=" + company + ", activated="
				+ activated + "]";
	}

}
