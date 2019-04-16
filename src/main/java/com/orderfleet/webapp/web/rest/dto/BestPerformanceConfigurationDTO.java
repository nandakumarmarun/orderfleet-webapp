package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.BestPerformanceConfiguration;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.DocumentType;

/**
 * * A DTO for the BestPerformanceConfiguration entity.
 *
 * @author Sarath
 * @since Mar 27, 2018
 *
 */
public class BestPerformanceConfigurationDTO {

	private String documentPid;
	private String documentName;
	private String companyPid;
	private String companyName;
	private DocumentType documentType;
	private BestPerformanceType bestPerformanceType;
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;

	public BestPerformanceConfigurationDTO() {
		super();
	}

	public BestPerformanceConfigurationDTO(BestPerformanceConfiguration bestPerformanceConfiguration) {
		super();
		this.documentPid = bestPerformanceConfiguration.getDocument().getPid();
		this.documentName = bestPerformanceConfiguration.getDocument().getName();
		this.companyPid = bestPerformanceConfiguration.getCompany().getPid();
		this.companyName = bestPerformanceConfiguration.getCompany().getLegalName();
		this.documentType = bestPerformanceConfiguration.getDocumentType();
		this.bestPerformanceType = bestPerformanceConfiguration.getBestPerformanceType();
		this.createdDate = bestPerformanceConfiguration.getCreatedDate();
		this.lastModifiedDate = bestPerformanceConfiguration.getLastModifiedDate();
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

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public BestPerformanceType getBestPerformanceType() {
		return bestPerformanceType;
	}

	public void setBestPerformanceType(BestPerformanceType bestPerformanceType) {
		this.bestPerformanceType = bestPerformanceType;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public String toString() {
		return "BestPerformanceConfigurationDTO [documentPid=" + documentPid + ", documentName=" + documentName
				+ ", companyPid=" + companyPid + ", companyName=" + companyName + ", documentType=" + documentType
				+ ", bestPerformanceType=" + bestPerformanceType + ", createdDate=" + createdDate
				+ ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}
