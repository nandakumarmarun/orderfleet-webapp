package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

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
 * A DocumentStockLocationSource.
 * 
 * @author Muhammed Riyas T
 * @since July 18, 2016
 */
@Entity
@Table(name = "tbl_document_stock_location_source")
public class DocumentStockLocationSource implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_stock_location_source_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_stock_location_source_id") })
	@GeneratedValue(generator = "seq_document_stock_location_source_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_stock_location_source_id')")
	private Long id;

	@ManyToOne
	private Document document;

	@ManyToOne
	@NotNull
	private StockLocation stockLocation;

	@ManyToOne
	private Company company;

	@Column(name = "is_default", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean isDefault;

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

	public DocumentStockLocationSource() {
		super();
	}

	public DocumentStockLocationSource(Document document, StockLocation stockLocation, Company company,
			boolean isDefault) {
		super();
		this.document = document;
		this.stockLocation = stockLocation;
		this.company = company;
		this.isDefault = isDefault;
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

	public StockLocation getStockLocation() {
		return stockLocation;
	}

	public void setStockLocation(StockLocation stockLocation) {
		this.stockLocation = stockLocation;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DocumentStockLocationSource documentStockLocationSource = (DocumentStockLocationSource) o;
		if (documentStockLocationSource.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, documentStockLocationSource.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
