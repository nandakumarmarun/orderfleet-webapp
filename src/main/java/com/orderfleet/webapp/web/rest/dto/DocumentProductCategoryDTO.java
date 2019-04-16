package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentProductCategory;

/**
 * A DTO for the DocumentProductCategory entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public class DocumentProductCategoryDTO {

	private String documentPid;

	private String documentName;

	private String productCategoryPid;

	private String productCategoryName;

	private LocalDateTime lastModifiedDate;

	public DocumentProductCategoryDTO() {
		super();
	}

	public DocumentProductCategoryDTO(DocumentProductCategory documentProductCategory) {
		super();
		this.documentPid = documentProductCategory.getDocument().getPid();
		this.documentName = documentProductCategory.getDocument().getName();
		this.productCategoryPid = documentProductCategory.getProductCategory().getPid();
		this.productCategoryName = documentProductCategory.getProductCategory().getName();
		this.lastModifiedDate = documentProductCategory.getLastModifiedDate();
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

	public String getProductCategoryPid() {
		return productCategoryPid;
	}

	public void setProductCategoryPid(String productCategoryPid) {
		this.productCategoryPid = productCategoryPid;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
