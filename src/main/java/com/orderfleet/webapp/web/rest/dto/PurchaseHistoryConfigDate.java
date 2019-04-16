package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

public class PurchaseHistoryConfigDate {

	private Long id;

	private String label;

	private String name;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private int sortOrder;

	public PurchaseHistoryConfigDate(Long id, String label, LocalDateTime startDate, LocalDateTime endDate, String name,
			int sortOrder) {
		super();
		this.id = id;
		this.label = label;
		this.startDate = startDate;
		this.endDate = endDate;
		this.name = name;
		this.sortOrder = sortOrder;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public String getName() {
		return name;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}
