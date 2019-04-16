package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DynamicDocumentReportDetail;
import com.orderfleet.webapp.report.enums.TableName;

/**
 * A DTO For DynamicDocumentReportDetail.
 *
 * @author Sarath
 * @since Mar 26, 2018
 *
 */
public class DynamicDocumentReportDetailDTO {

	private String dynamicDocumentSettingsHeaderPid;
	private String formElementPid;
	private String formElementName;
	private Integer sortOrder = 0;
	private TableName tableName;
	private String columnName;
	private String displayLabel;
	private LocalDateTime createdDate = LocalDateTime.now();

	public DynamicDocumentReportDetailDTO() {
		super();
	}

	public DynamicDocumentReportDetailDTO(DynamicDocumentReportDetail documentReportDetail) {
		super();
		this.dynamicDocumentSettingsHeaderPid = documentReportDetail.getDynamicDocumentSettingsHeader().getPid();
		this.formElementPid = documentReportDetail.getFormElement().getPid();
		this.formElementName = documentReportDetail.getFormElement().getName();
		this.sortOrder = documentReportDetail.getSortOrder();
		this.tableName = documentReportDetail.getTableName();
		this.columnName = documentReportDetail.getColumnName();
		this.displayLabel = documentReportDetail.getDisplayLabel();
		this.createdDate = documentReportDetail.getCreatedDate();
	}

	public String getDynamicDocumentSettingsHeaderPid() {
		return dynamicDocumentSettingsHeaderPid;
	}

	public void setDynamicDocumentSettingsHeaderPid(String dynamicDocumentSettingsHeaderPid) {
		this.dynamicDocumentSettingsHeaderPid = dynamicDocumentSettingsHeaderPid;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
	}

	public String getFormElementName() {
		return formElementName;
	}

	public void setFormElementName(String formElementName) {
		this.formElementName = formElementName;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "DynamicDocumentReportDetailDTO [dynamicDocumentSettingsHeaderPid=" + dynamicDocumentSettingsHeaderPid
				+ ", formElementPid=" + formElementPid + ", formElementName=" + formElementName + ", sortOrder="
				+ sortOrder + ", tableName=" + tableName + ", columnName=" + columnName + ", displayLabel="
				+ displayLabel + ", createdDate=" + createdDate + "]";
	}

}
