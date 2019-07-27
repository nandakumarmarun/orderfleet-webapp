package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class ProductGroupLocationPerformaceDTO {

	private List<String> monthList;

	private Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargets;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<ProductGroupLocationTargetDTO>> getProductGroupLocationTargets() {
		return productGroupLocationTargets;
	}

	public void setProductGroupLocationTargets(
			Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargets) {
		this.productGroupLocationTargets = productGroupLocationTargets;
	}

}
