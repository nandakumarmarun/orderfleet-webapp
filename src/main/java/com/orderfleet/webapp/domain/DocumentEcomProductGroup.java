package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Document ProductGroup
 * 
 * @author Anish
 * @since July 08 2020
 */
@Entity
@Table(name = "tbl_document_ecom_product_group")
public class DocumentEcomProductGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_ecom_product_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_ecom_product_group_id") })
	@GeneratedValue(generator = "seq_document_ecom_product_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_ecom_product_group_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private EcomProductGroup productGroup;

	@NotNull
	@Column(name = "sort_order", nullable = false, columnDefinition = "integer DEFAULT 0 ")
	private int sortOrder;

	@NotNull
	@ManyToOne
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public DocumentEcomProductGroup() {
		super();
	}

	public DocumentEcomProductGroup(Document document, EcomProductGroup productGroup, Company company, int sortOrder) {
		super();
		this.document = document;
		this.productGroup = productGroup;
		this.company = company;
		this.sortOrder = sortOrder;
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

	public EcomProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(EcomProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

}
