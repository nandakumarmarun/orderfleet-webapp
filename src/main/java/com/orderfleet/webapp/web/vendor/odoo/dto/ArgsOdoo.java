package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ArgsOdoo {

	private Object companyId;

	public Object getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Object companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		return "Args [companyId=" + companyId + "]";
	}

}
