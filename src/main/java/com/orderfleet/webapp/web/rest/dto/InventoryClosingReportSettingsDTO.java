package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.InventoryClosingReportSettings;

public class InventoryClosingReportSettingsDTO {

private Long id;
	
	private String documentPid;
	
	private String documentName;
	
	private String inventoryClosingReportSettingGroupPid;
	
	private String inventoryClosingReportSettingGroupName;
	
	public InventoryClosingReportSettingsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryClosingReportSettingsDTO(Long id, String documentPid, String documentName
			) {
		super();
		this.id = id;
		this.documentPid = documentPid;
		this.documentName = documentName;
		
	}
	
	public InventoryClosingReportSettingsDTO(InventoryClosingReportSettings inventoryClosingReport) {
		super();
		this.id = inventoryClosingReport.getId();
		this.documentPid = inventoryClosingReport.getDocument().getPid();
		this.documentName = inventoryClosingReport.getDocument().getName();
		this.inventoryClosingReportSettingGroupPid=inventoryClosingReport.getInventoryClosingReportSettingGroup().getPid();
		this.inventoryClosingReportSettingGroupName=inventoryClosingReport.getInventoryClosingReportSettingGroup().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	

	public String getInventoryClosingReportSettingGroupPid() {
		return inventoryClosingReportSettingGroupPid;
	}

	public void setInventoryClosingReportSettingGroupPid(String inventoryClosingReportSettingGroupPid) {
		this.inventoryClosingReportSettingGroupPid = inventoryClosingReportSettingGroupPid;
	}

	public String getInventoryClosingReportSettingGroupName() {
		return inventoryClosingReportSettingGroupName;
	}

	public void setInventoryClosingReportSettingGroupName(String inventoryClosingReportSettingGroupName) {
		this.inventoryClosingReportSettingGroupName = inventoryClosingReportSettingGroupName;
	}

}
