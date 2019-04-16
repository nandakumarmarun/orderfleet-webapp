package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.PaymentMode;

public class AccountingVoucherUIDTO {

	private String activityPid;
	
	private String documentPid;
	
	private PaymentMode paymentMode;
	
	private String byAccount;
	
	private String toAccount;
	
	private String date;
	
	private String remark;
	
	private Long amount;

	public AccountingVoucherUIDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountingVoucherUIDTO(String activityPid, String documentPid, PaymentMode paymentMode, String byAccount,
			String toAccount, String date, String remark, Long amount) {
		super();
		this.activityPid = activityPid;
		this.documentPid = documentPid;
		this.paymentMode = paymentMode;
		this.byAccount = byAccount;
		this.toAccount = toAccount;
		this.date = date;
		this.remark = remark;
		this.amount = amount;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getByAccount() {
		return byAccount;
	}

	public void setByAccount(String byAccount) {
		this.byAccount = byAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "AccountingVoucherUIDTO [activityPid=" + activityPid + ", documentPid=" + documentPid + ", paymentMode="
				+ paymentMode + ", byAccount=" + byAccount + ", toAccount=" + toAccount + ", date=" + date
				+ ", remark=" + remark + ", amount=" + amount + "]";
	}
	
	
}
