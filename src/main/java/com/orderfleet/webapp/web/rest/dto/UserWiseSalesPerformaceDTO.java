package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class UserWiseSalesPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<UserWiseSalesTargetDTO>> userWiseSalesTargets;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<UserWiseSalesTargetDTO>> getUserWiseSalesTargets() {
		return userWiseSalesTargets;
	}

	public void setUserWiseSalesTargets(Map<String, List<UserWiseSalesTargetDTO>> userWiseSalesTargets) {
		this.userWiseSalesTargets = userWiseSalesTargets;
	}

}
