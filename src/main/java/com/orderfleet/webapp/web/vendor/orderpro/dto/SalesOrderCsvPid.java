package com.orderfleet.webapp.web.vendor.orderpro.dto;

import java.util.List;

public class SalesOrderCsvPid {

	private List<String> salesPid;

	
	public List<String> getSalesPid() {
		return salesPid;
	}

	public void setSalesPid(List<String> salesPid) {
		this.salesPid = salesPid;
	}

	@Override
	public String toString() {
		return "SalesOrderPid [salesPid=" + salesPid + "]";
	}
	
	
}
