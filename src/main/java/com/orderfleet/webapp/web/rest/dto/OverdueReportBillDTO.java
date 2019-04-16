package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

public class OverdueReportBillDTO {

	private String pid;

	private String billNumber;

	private LocalDate date;

	private double amount;

	public OverdueReportBillDTO() {
	}

	public OverdueReportBillDTO(String pid, String billNumber, LocalDate date, double amount) {
		super();
		this.pid = pid;
		this.billNumber = billNumber;
		this.date = date;
		this.amount = amount;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
