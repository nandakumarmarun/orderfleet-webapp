package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class CustomerGropuPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<UserCustomerGroupTargetDTO>> customerGroupUserTargets;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<UserCustomerGroupTargetDTO>> getCustomerGroupUserTargets() {
		return customerGroupUserTargets;
	}

	public void setCustomerGroupUserTargets(Map<String, List<UserCustomerGroupTargetDTO>> customerGroupUserTargets) {
		this.customerGroupUserTargets = customerGroupUserTargets;
	}
	

}
