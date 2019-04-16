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
 * A ReferenceDocument
 *
 * @author Sarath
 * @since Oct 19, 2016
 */

@Entity
@Table(name = "tbl_reference_document")
public class ReferenceDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_reference_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_reference_document_id") })
	@GeneratedValue(generator = "seq_reference_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_reference_document_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private Document referenceDocument;

	@NotNull
	@ManyToOne
	private Company company;

	public ReferenceDocument(Document document, Document referenceDocument, Company company) {
		super();
		this.document = document;
		this.referenceDocument = referenceDocument;
		this.company = company;
	}

	public ReferenceDocument() {
		super();
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

	public Document getReferenceDocument() {
		return referenceDocument;
	}

	public void setReferenceDocument(Document referenceDocument) {
		this.referenceDocument = referenceDocument;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "ReferenceDocument [id=" + id + ", document=" + document + ", referenceDocument=" + referenceDocument
				+ ", company=" + company + "]";
	}

}
