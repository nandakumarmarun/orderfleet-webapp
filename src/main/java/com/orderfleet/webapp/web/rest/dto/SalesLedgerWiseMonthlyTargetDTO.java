package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Oct 15, 2016
 */
public class SalesLedgerWiseMonthlyTargetDTO {

	private String salesLedgerPid;
	private String salesLedgerName;
	private String salesLedgerWiseTargetPid;
	private double amount;
	private String monthAndYear;

	public SalesLedgerWiseMonthlyTargetDTO() {
		super();
	}

	public SalesLedgerWiseMonthlyTargetDTO(String salesLedgerPid, String salesLedgerName,
			String salesLedgerWiseTargetPid, double amount, String monthAndYear) {
		super();
		this.salesLedgerPid = salesLedgerPid;
		this.salesLedgerName = salesLedgerName;
		this.salesLedgerWiseTargetPid = salesLedgerWiseTargetPid;
		this.amount = amount;
		this.monthAndYear = monthAndYear;
	}

	public String getSalesLedgerPid() {
		return salesLedgerPid;
	}

	public void setSalesLedgerPid(String salesLedgerPid) {
		this.salesLedgerPid = salesLedgerPid;
	}

	public String getSalesLedgerName() {
		return salesLedgerName;
	}

	public void setSalesLedgerName(String salesLedgerName) {
		this.salesLedgerName = salesLedgerName;
	}

	public String getSalesLedgerWiseTargetPid() {
		return salesLedgerWiseTargetPid;
	}

	public void setSalesLedgerWiseTargetPid(String salesLedgerWiseTargetPid) {
		this.salesLedgerWiseTargetPid = salesLedgerWiseTargetPid;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMonthAndYear() {
		return monthAndYear;
	}

	public void setMonthAndYear(String monthAndYear) {
		this.monthAndYear = monthAndYear;
	}

}
