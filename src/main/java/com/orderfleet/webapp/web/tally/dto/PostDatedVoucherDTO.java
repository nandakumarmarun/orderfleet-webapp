package com.orderfleet.webapp.web.tally.dto;

public class PostDatedVoucherDTO {

	private String voucherTypeName;
	private String partyLedgerName;
	private String voucherNumber;
	private double amount;
	private String date;
	private String narration;
	private String billName;
	private double billAmount;
	
	
	public String getVoucherTypeName() {
		return voucherTypeName;
	}
	public void setVoucherTypeName(String voucherTypeName) {
		this.voucherTypeName = voucherTypeName;
	}
	public String getPartyLedgerName() {
		return partyLedgerName;
	}
	public void setPartyLedgerName(String partyLedgerName) {
		this.partyLedgerName = partyLedgerName;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getBillName() {
		return billName;
	}
	public void setBillName(String billName) {
		this.billName = billName;
	}
	public double getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(double billAmount) {
		this.billAmount = billAmount;
	}
	
	
}
