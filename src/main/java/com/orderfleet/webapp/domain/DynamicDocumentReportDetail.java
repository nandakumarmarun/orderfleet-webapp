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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.report.enums.TableName;

/**
 * A DynamicDocumentReportDetail.
 *
 * @author Shaheer
 *
 */
@Entity
@Table(name = "tbl_dynamic_document_report_detail")
public class DynamicDocumentReportDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dynamic_document_report_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dynamic_document_report_detail_id") })
	@GeneratedValue(generator = "seq_dynamic_document_report_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dynamic_document_report_detail_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader;

	@ManyToOne
	private FormElement formElement;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "table_name", nullable = false)
	private TableName tableName;

	@Column(name = "column_name")
	private String columnName;

	@Column(name = "display_label")
	private String displayLabel;

	@NotNull
	@Column(name = "sort_order", nullable = false, columnDefinition = "integer DEFAULT 0 ")
	private int sortOrder;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DynamicDocumentSettingsHeader getDynamicDocumentSettingsHeader() {
		return dynamicDocumentSettingsHeader;
	}

	public void setDynamicDocumentSettingsHeader(DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader) {
		this.dynamicDocumentSettingsHeader = dynamicDocumentSettingsHeader;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	public TableName getTableName() {
		return tableName;
	}

	public void setTableName(TableName tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
