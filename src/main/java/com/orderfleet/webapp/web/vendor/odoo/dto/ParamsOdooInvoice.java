package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ParamsOdooInvoice {

	private boolean create_multi;
	private List<OdooInvoice> invoices;

	public boolean getCreate_multi() {
		return create_multi;
	}

	public void setCreate_multi(boolean create_multi) {
		this.create_multi = create_multi;
	}

	public List<OdooInvoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<OdooInvoice> invoices) {
		this.invoices = invoices;
	}

}
