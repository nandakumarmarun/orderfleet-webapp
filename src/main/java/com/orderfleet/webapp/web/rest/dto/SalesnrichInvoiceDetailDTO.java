package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.SalesnrichInvoiceDetail;

/**
 * A DTO for the SalesnrichInvoiceDetail entity.
 *
 * @author Sarath
 * @since Apr 5, 2018
 *
 */
public class SalesnrichInvoiceDetailDTO {

	private String companyPid;
	private String companyName;
	private String particulars;
	private double quantity;
	private double price;
	private double total;

	public SalesnrichInvoiceDetailDTO() {
		super();
	}

	public SalesnrichInvoiceDetailDTO(SalesnrichInvoiceDetail salesnrichInvoiceDetail) {
		super();
		this.companyPid = salesnrichInvoiceDetail.getCompany().getPid();
		this.companyName = salesnrichInvoiceDetail.getCompany().getLegalName();
		this.particulars = salesnrichInvoiceDetail.getParticulars();
		this.quantity = salesnrichInvoiceDetail.getQuantity();
		this.price = salesnrichInvoiceDetail.getPrice();
		this.total = salesnrichInvoiceDetail.getTotal();
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "SalesnrichInvoiceDetailDTO [companyPid=" + companyPid + ", companyName=" + companyName
				+ ", particulars=" + particulars + ", quantity=" + quantity + ", price=" + price + ", total=" + total
				+ "]";
	}

}
