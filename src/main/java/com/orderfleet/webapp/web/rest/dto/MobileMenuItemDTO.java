package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.MobileMenuItem;

/**
 * A DTO for the AccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public class MobileMenuItemDTO {

	private Long id;

	private String name;

	private String label;

	private LocalDateTime lastModifiedDate;
	
	public MobileMenuItemDTO() {
		super();
	}

	public MobileMenuItemDTO(MobileMenuItem mobileMenuItem) {
		super();
		this.id = mobileMenuItem.getId();
		this.name = mobileMenuItem.getName();
		this.label = mobileMenuItem.getLabel();
	}
	
	public MobileMenuItemDTO(Long id, String name, String label,LocalDateTime lastModifiedDate) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
		this.lastModifiedDate=lastModifiedDate;
		
	}

	public MobileMenuItemDTO(String name, String label,LocalDateTime lastModifiedDate) {
		super();
		this.name = name;
		this.label = label;
		this.lastModifiedDate=lastModifiedDate;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
