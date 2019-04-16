package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class BillDetailsDTO {

	private String companyPid;

	private int checkedUserCount;

	private int activeUserCount;

	private int totalUserCount;

	private Long invoiceNumber;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate invoiceDate;
	
	private String particulars;

	private String amountType;

	private double amountPerUser;

	private double fixedAmount;

	private double subTotal;

	private double gstPercentage;

	private double gstAmount;

	private double totalAmount;

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public int getCheckedUserCount() {
		return checkedUserCount;
	}

	public void setCheckedUserCount(int checkedUserCount) {
		this.checkedUserCount = checkedUserCount;
	}

	public int getActiveUserCount() {
		return activeUserCount;
	}

	public void setActiveUserCount(int activeUserCount) {
		this.activeUserCount = activeUserCount;
	}

	public int getTotalUserCount() {
		return totalUserCount;
	}

	public void setTotalUserCount(int totalUserCount) {
		this.totalUserCount = totalUserCount;
	}

	public Long getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(Long invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public double getAmountPerUser() {
		return amountPerUser;
	}

	public void setAmountPerUser(double amountPerUser) {
		this.amountPerUser = amountPerUser;
	}

	public double getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(double fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getGstPercentage() {
		return gstPercentage;
	}

	public void setGstPercentage(double gstPercentage) {
		this.gstPercentage = gstPercentage;
	}

	public double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}


}
