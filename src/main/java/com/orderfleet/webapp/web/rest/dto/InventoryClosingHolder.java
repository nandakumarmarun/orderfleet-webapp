package com.orderfleet.webapp.web.rest.dto;

import java.util.List;


public class InventoryClosingHolder {
	
	private List<InventoryClosingDetailProductDTO> inventoryClosingDetailProducts;
	
	private List<InventoryClosingProductDetailSettingGroupDTO> inventoryClosingProductDetailSettingGroups;
	
	private String selectedUserPid;

	public List<InventoryClosingDetailProductDTO> getInventoryClosingDetailProducts() {
		return inventoryClosingDetailProducts;
	}

	public void setInventoryClosingDetailProducts(List<InventoryClosingDetailProductDTO> inventoryClosingDetailProducts) {
		this.inventoryClosingDetailProducts = inventoryClosingDetailProducts;
	}

	public List<InventoryClosingProductDetailSettingGroupDTO> getInventoryClosingProductDetailSettingGroups() {
		return inventoryClosingProductDetailSettingGroups;
	}

	public void setInventoryClosingProductDetailSettingGroups(
			List<InventoryClosingProductDetailSettingGroupDTO> inventoryClosingProductDetailSettingGroups) {
		this.inventoryClosingProductDetailSettingGroups = inventoryClosingProductDetailSettingGroups;
	}

	public String getSelectedUserPid() {
		return selectedUserPid;
	}

	public void setSelectedUserPid(String selectedUserPid) {
		this.selectedUserPid = selectedUserPid;
	}

}
