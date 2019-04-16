package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Nov 15, 2016
 */
public class TourPaymentDTO {

	private String voucherNumber;
	private String paymentType;
	private String chequeNo;
	private String chequeDate;
	private Double chequeAmount;
	private String bankName;
	private String bankPlace;
	private String bankCode;
	private String prDate;
	private String company;

	public TourPaymentDTO() {
		super();
	}

	public TourPaymentDTO(String voucherNumber, String paymentType, String chequeNo, String chequeDate,
			Double chequeAmount, String bankName, String bankPlace, String bankCode, String prDate, String company) {
		super();
		this.voucherNumber = voucherNumber;
		this.paymentType = paymentType;
		this.chequeNo = chequeNo;
		this.chequeDate = chequeDate;
		this.chequeAmount = chequeAmount;
		this.bankName = bankName;
		this.bankPlace = bankPlace;
		this.bankCode = bankCode;
		this.prDate = prDate;
		this.company = company;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public Double getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(Double chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankPlace() {
		return bankPlace;
	}

	public void setBankPlace(String bankPlace) {
		this.bankPlace = bankPlace;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getPrDate() {
		return prDate;
	}

	public void setPrDate(String prDate) {
		this.prDate = prDate;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
