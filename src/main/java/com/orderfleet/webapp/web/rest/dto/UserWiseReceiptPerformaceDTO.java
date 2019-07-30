package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class UserWiseReceiptPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<UserWiseReceiptTargetDTO>> userWiseReceiptTargets;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<UserWiseReceiptTargetDTO>> getUserWiseReceiptTargets() {
		return userWiseReceiptTargets;
	}

	public void setUserWiseReceiptTargets(Map<String, List<UserWiseReceiptTargetDTO>> userWiseReceiptTargets) {
		this.userWiseReceiptTargets = userWiseReceiptTargets;
	}

}
