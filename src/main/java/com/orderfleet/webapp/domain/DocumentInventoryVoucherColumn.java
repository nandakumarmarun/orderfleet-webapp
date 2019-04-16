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
 * A DocumentInventoryVoucher
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 */
@Entity
@Table(name = "tbl_document_inventory_voucher_column")
public class DocumentInventoryVoucherColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_inventory_voucher_column_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_inventory_voucher_column_id") })
	@GeneratedValue(generator = "seq_document_inventory_voucher_column_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_inventory_voucher_column_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private InventoryVoucherColumn inventoryVoucherColumn;

	@NotNull
	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@NotNull
	@ManyToOne
	private Company company;

	@Column(name = "label", length = 300)
	private String label;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public DocumentInventoryVoucherColumn() {

	}

	public DocumentInventoryVoucherColumn(Document document, InventoryVoucherColumn inventoryVoucherColumn,
			boolean enabled, Company company, String label) {
		super();
		this.document = document;
		this.inventoryVoucherColumn = inventoryVoucherColumn;
		this.enabled = enabled;
		this.company = company;
		this.label = label;
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

	public InventoryVoucherColumn getInventoryVoucherColumn() {
		return inventoryVoucherColumn;
	}

	public void setInventoryVoucherColumn(InventoryVoucherColumn inventoryVoucherColumn) {
		this.inventoryVoucherColumn = inventoryVoucherColumn;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
