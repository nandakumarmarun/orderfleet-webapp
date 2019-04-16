package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.DocumentProductGroup;

/**
 * A DTO for the DocumentProductGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public class DocumentProductGroupDTO {

	private String documentPid;

	private String documentName;

	private String productGroupPid;

	private String productGroupName;

	private int sortOrder;

	private LocalDateTime lastModifiedDate;

	public DocumentProductGroupDTO() {
		super();
	}

	public DocumentProductGroupDTO(DocumentProductGroup documentProductGroup) {
		super();
		this.documentPid = documentProductGroup.getDocument().getPid();
		this.documentName = documentProductGroup.getDocument().getName();
		this.productGroupPid = documentProductGroup.getProductGroup().getPid();
		this.productGroupName = documentProductGroup.getProductGroup().getName();
		this.sortOrder = documentProductGroup.getSortOrder();
		this.lastModifiedDate = documentProductGroup.getLastModifiedDate();
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

	public String getProductGroupPid() {
		return productGroupPid;
	}

	public void setProductGroupPid(String productGroupPid) {
		this.productGroupPid = productGroupPid;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
