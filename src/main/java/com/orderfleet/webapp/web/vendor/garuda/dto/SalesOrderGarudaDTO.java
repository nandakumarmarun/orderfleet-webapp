package com.orderfleet.webapp.web.vendor.garuda.dto;

import java.util.List;

public class SalesOrderGarudaDTO {

	private String invoiceNo;
	
	private String invoiceDate;
	
	private String pid;
	
	private String customerCode;
	
	private String employeeName;
	
	private String orderType;
	
	private Double total;
	
	private List<SalesOrderDetailGarudaDTO>  salesOrderdetails;

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public List<SalesOrderDetailGarudaDTO> getSalesOrderdetails() {
		return salesOrderdetails;
	}

	public void setSalesOrderdetails(List<SalesOrderDetailGarudaDTO> salesOrderdetails) {
		this.salesOrderdetails = salesOrderdetails;
	}
}
