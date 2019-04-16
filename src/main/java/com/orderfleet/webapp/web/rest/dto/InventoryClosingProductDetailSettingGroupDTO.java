package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.Flow;

public class InventoryClosingProductDetailSettingGroupDTO {

	private Long id;
	
	private String inventoryClosingDetailProductPid;
	
	private String inventoryClosingReportSettingGroupPid;
	
	private Flow flow;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInventoryClosingDetailProductPid() {
		return inventoryClosingDetailProductPid;
	}

	public void setInventoryClosingDetailProductPid(String inventoryClosingDetailProductPid) {
		this.inventoryClosingDetailProductPid = inventoryClosingDetailProductPid;
	}

	public String getInventoryClosingReportSettingGroupPid() {
		return inventoryClosingReportSettingGroupPid;
	}

	public void setInventoryClosingReportSettingGroupPid(String inventoryClosingReportSettingGroupPid) {
		this.inventoryClosingReportSettingGroupPid = inventoryClosingReportSettingGroupPid;
	}

	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	@Override
	public String toString() {
		return "InventoryClosingProductDetailSettingGroupDTO [id=" + id + ", inventoryClosingDetailProductPid="
				+ inventoryClosingDetailProductPid + ", inventoryClosingReportSettingGroupPid="
				+ inventoryClosingReportSettingGroupPid + ", flow=" + flow + "]";
	}
	
	
}
