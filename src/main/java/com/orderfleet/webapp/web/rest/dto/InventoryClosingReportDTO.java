package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class InventoryClosingReportDTO {

	List<InventoryClosingReportSettingGroupDTO> reportSettingGroupDTOHeaders;
	
	Map<String, List<Double>> productQuantityMap;

	public List<InventoryClosingReportSettingGroupDTO> getReportSettingGroupDTOHeaders() {
		return reportSettingGroupDTOHeaders;
	}

	public void setReportSettingGroupDTOHeaders(List<InventoryClosingReportSettingGroupDTO> reportSettingGroupDTOHeaders) {
		this.reportSettingGroupDTOHeaders = reportSettingGroupDTOHeaders;
	}

	public Map<String, List<Double>> getProductQuantityMap() {
		return productQuantityMap;
	}

	public void setProductQuantityMap(Map<String, List<Double>> productQuantityMap) {
		this.productQuantityMap = productQuantityMap;
	}

	
}
