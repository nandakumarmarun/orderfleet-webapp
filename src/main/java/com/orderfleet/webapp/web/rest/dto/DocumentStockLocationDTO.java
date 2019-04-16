package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentStockLocationDestination;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;

/**
 * A DTO for the DocumentStockLocationSource and
 * DocumentStockLocationDestination entity.
 * 
 * @author Shaheer
 * @since September 22, 2016
 */
public class DocumentStockLocationDTO {

	private String documentPid;

	private String documentName;

	private String stockLocationPid;

	private String stockLocationName;

	private boolean isDefault;

	private LocalDateTime lastModifiedDate;

	public DocumentStockLocationDTO() {
		super();
	}

	public DocumentStockLocationDTO(DocumentStockLocationDestination documentStockLocation) {
		super();
		this.documentPid = documentStockLocation.getDocument().getPid();
		this.documentName = documentStockLocation.getDocument().getName();
		this.stockLocationPid = documentStockLocation.getStockLocation().getPid();
		this.stockLocationName = documentStockLocation.getStockLocation().getName();
		this.isDefault = documentStockLocation.getIsDefault();
		this.lastModifiedDate = documentStockLocation.getLastModifiedDate();
	}

	public DocumentStockLocationDTO(DocumentStockLocationSource documentStockLocation) {
		super();
		this.documentPid = documentStockLocation.getDocument().getPid();
		this.documentName = documentStockLocation.getDocument().getName();
		this.stockLocationPid = documentStockLocation.getStockLocation().getPid();
		this.stockLocationName = documentStockLocation.getStockLocation().getName();
		this.isDefault = documentStockLocation.getIsDefault();
		this.lastModifiedDate = documentStockLocation.getLastModifiedDate();
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getStockLocationPid() {
		return stockLocationPid;
	}

	public void setStockLocationPid(String stockLocationPid) {
		this.stockLocationPid = stockLocationPid;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
