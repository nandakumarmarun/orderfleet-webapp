package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.MobileMasterItem;

public class MobileMasterDetailDTO {

	private MobileMasterItem mobileMasterItem;
	private String operationTime;
	private Long count;
	
	public MobileMasterItem getMobileMasterItem() {
		return mobileMasterItem;
	}
	public void setMobileMasterItem(MobileMasterItem mobileMasterItem) {
		this.mobileMasterItem = mobileMasterItem;
	}
	public String getOperationTime() {
		return operationTime;
	}
	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
	
}
