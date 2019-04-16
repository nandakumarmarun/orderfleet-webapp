package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.DocumentType;

public class OrderStatusDTO {

	private Long id;

	private String name;

	private boolean active;
	
	private DocumentType documentType;

	public OrderStatusDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrderStatusDTO(Long id, String name, boolean active, DocumentType documentType) {
		super();
		this.id = id;
		this.name = name;
		this.active = active;
		this.documentType = documentType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	@Override
	public String toString() {
		return "OrderStatusDTO [id=" + id + ", name=" + name + ", active=" + active + "]";
	}

}
