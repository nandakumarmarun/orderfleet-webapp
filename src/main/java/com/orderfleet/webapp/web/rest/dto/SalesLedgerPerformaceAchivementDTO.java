package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Map;

public class SalesLedgerPerformaceAchivementDTO {

	private List<String> monthList;

	private Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargets;

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<String, List<SalesLedgerWiseTargetDTO>> getSalesLedgerWiseTargets() {
		return salesLedgerWiseTargets;
	}

	public void setSalesLedgerWiseTargets(
			Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargets) {
		this.salesLedgerWiseTargets = salesLedgerWiseTargets;
	}

	@Override
	public String toString() {
		return "SalesPerformaceAchivementDTO [monthList=" + monthList + ", salesLedgerWiseTargets="
				+ salesLedgerWiseTargets + "]";
	}

}
