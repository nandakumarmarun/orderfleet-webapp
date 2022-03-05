package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Balance_Amt","Customer_code","Due_Date","Invoice_Amt","Voucher_Date","Voucher_No"})
public class OutStandingFocus {

	@JsonProperty("Balance_Amt")
	private String balanceAmount;

	@JsonProperty("Customer_code")
	private String customerCode;

	@JsonProperty("Due_Date")
	private String dueDate;

	@JsonProperty("Invoice_Amt")
	private String invoiceAmount;

	@JsonProperty("Voucher_Date")
	private String voucherDate;
	
	@JsonProperty("Voucher_No")
	private String voucherNo;

	/**
	 * @return the balanceAmount
	 */
	@JsonProperty("Balance_Amt")
	public String getBalanceAmount() {
		return balanceAmount;
	}

	/**
	 * @param balanceAmount the balanceAmount to set
	 */
	@JsonProperty("Balance_Amt")
	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	/**
	 * @return the customerCode
	 */
	@JsonProperty("Customer_code")
	public String getCustomerCode() {
		return customerCode;
	}

	/**
	 * @param customerCode the customerCode to set
	 */
	@JsonProperty("Customer_code")
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	/**
	 * @return the dueDate
	 */
	@JsonProperty("Due_Date")
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	@JsonProperty("Due_Date")
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return the invoiceAmount
	 */
	@JsonProperty("Invoice_Amt")
	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	@JsonProperty("Invoice_Amt")
	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the voucherDate
	 */
	@JsonProperty("Voucher_Date")
	public String getVoucherDate() {
		return voucherDate;
	}

	/**
	 * @param voucherDate the voucherDate to set
	 */
	@JsonProperty("Voucher_Date")
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}

	/**
	 * @return the voucherNo
	 */
	@JsonProperty("Voucher_No")
	public String getVoucherNo() {
		return voucherNo;
	}

	/**
	 * @param voucherNo the voucherNo to set
	 */
	@JsonProperty("Voucher_No")
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	
	
}
