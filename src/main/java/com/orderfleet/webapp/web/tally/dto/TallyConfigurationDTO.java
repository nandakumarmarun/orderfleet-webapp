package com.orderfleet.webapp.web.tally.dto;

import com.orderfleet.webapp.domain.TallyConfiguration;

public class TallyConfigurationDTO {

	private String pid;
	private String tallyCompanyName;
	private String dynamicDate;
	private boolean orderNumberWithEmployee = false;
	private boolean actualBillStatus = false;
	private String gstNames ;
	private String staticGodownNames;
	private String roundOffLedgerName;
	private String salesLedgerName;
	private String tallyProductKey;
	private String receiptVoucherType;
	private String bankReceiptType;
	private String bankName; //bank receipt bank name
	private String cashReceiptType;
	private String transactionType;//transaction type for bank receipt
	private boolean itemRemarksEnabled;
	private String pdcVoucherType;
	private String companyName;
	private String companyPid;
	
	
	public TallyConfigurationDTO() {
		super();
	}

	public TallyConfigurationDTO(TallyConfiguration tally) {
		super();
		this.pid = tally.getPid();
		this.tallyCompanyName = tally.getTallyCompanyName();
		this.dynamicDate = tally.getDynamicDate();
		this.orderNumberWithEmployee = tally.getOrderNumberWithEmployee();
		this.actualBillStatus = tally.getActualBillStatus();
		this.gstNames = tally.getGstNames();
		this.staticGodownNames = tally.getStaticGodownNames();
		this.roundOffLedgerName = tally.getRoundOffLedgerName();
		this.salesLedgerName = tally.getSalesLedgerName();
		this.tallyProductKey = tally.getTallyProductKey();
		this.receiptVoucherType = tally.getReceiptVoucherType();
		this.bankReceiptType = tally.getBankReceiptType();
		this.bankName =  tally.getBankName();
		this.cashReceiptType = tally.getCashReceiptType();
		this.companyName = tally.getCompany().getLegalName();
		this.companyPid = tally.getCompany().getPid();
		this.transactionType = tally.getTransactionType();
		this.itemRemarksEnabled = tally.getItemRemarksEnabled();
		this.pdcVoucherType = tally.getPdcVoucherType();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getTallyCompanyName() {
		return tallyCompanyName;
	}

	public void setTallyCompanyName(String tallyCompanyName) {
		this.tallyCompanyName = tallyCompanyName;
	}

	public String getDynamicDate() {
		return dynamicDate;
	}

	public void setDynamicDate(String dynamicDate) {
		this.dynamicDate = dynamicDate;
	}

	public boolean getOrderNumberWithEmployee() {
		return orderNumberWithEmployee;
	}

	public void setOrderNumberWithEmployee(boolean orderNumberWithEmployee) {
		this.orderNumberWithEmployee = orderNumberWithEmployee;
	}

	public boolean getActualBillStatus() {
		return actualBillStatus;
	}

	public void setActualBillStatus(boolean actualBillStatus) {
		this.actualBillStatus = actualBillStatus;
	}

	public String getGstNames() {
		return gstNames;
	}

	public void setGstNames(String gstNames) {
		this.gstNames = gstNames;
	}

	public String getStaticGodownNames() {
		return staticGodownNames;
	}

	public void setStaticGodownNames(String staticGodownNames) {
		this.staticGodownNames = staticGodownNames;
	}

	

	public String getRoundOffLedgerName() {
		return roundOffLedgerName;
	}

	public void setRoundOffLedgerName(String roundOffLedgerName) {
		this.roundOffLedgerName = roundOffLedgerName;
	}

	public String getSalesLedgerName() {
		return salesLedgerName;
	}

	public void setSalesLedgerName(String salesLedgerName) {
		this.salesLedgerName = salesLedgerName;
	}

	public String getTallyProductKey() {
		return tallyProductKey;
	}

	public void setTallyProductKey(String tallyProductKey) {
		this.tallyProductKey = tallyProductKey;
	}

	public String getReceiptVoucherType() {
		return receiptVoucherType;
	}

	public void setReceiptVoucherType(String receiptVoucherType) {
		this.receiptVoucherType = receiptVoucherType;
	}
	
	public String getBankReceiptType() {
		return bankReceiptType;
	}

	public void setBankReceiptType(String bankReceiptType) {
		this.bankReceiptType = bankReceiptType;
	}
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCashReceiptType() {
		return cashReceiptType;
	}

	public void setCashReceiptType(String cashReceiptType) {
		this.cashReceiptType = cashReceiptType;
	}
	
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public boolean getItemRemarksEnabled() {
		return itemRemarksEnabled;
	}

	public void setItemRemarksEnabled(boolean itemRemarksEnabled) {
		this.itemRemarksEnabled = itemRemarksEnabled;
	}
	
	public String getPdcVoucherType() {
		return pdcVoucherType;
	}

	public void setPdcVoucherType(String pdcVoucherType) {
		this.pdcVoucherType = pdcVoucherType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}
	
	
}
