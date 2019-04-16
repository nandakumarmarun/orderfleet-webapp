package com.orderfleet.webapp.web.tally.dto;

import java.util.List;

import com.orderfleet.webapp.domain.enums.VoucherType;

public class SalesOrderTally {

	private String date;
	private String trimChar;
	private String ledgerName;
	private String ledgerAddress;
	private String priceLevelName;
	private double documentTotal;
	private String remarks;
	private String pid;
	private String employeeName;
	List<SalesItemTally> salesItemList;
	List<GstLedgerDTO> gstLedgers;
	private TallyConfigurationDTO tallyConfig;
	private VoucherType voucherType;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTrimChar() {
		return trimChar;
	}
	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}
	public String getLedgerAddress() {
		return ledgerAddress;
	}
	public void setLedgerAddress(String ledgerAddress) {
		this.ledgerAddress = ledgerAddress;
	}
	public String getPriceLevelName() {
		return priceLevelName;
	}
	public void setPriceLevelName(String priceLevelName) {
		this.priceLevelName = priceLevelName;
	}
	
	public double getDocumentTotal() {
		return documentTotal;
	}
	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public List<SalesItemTally> getSalesItemList() {
		return salesItemList;
	}
	public void setSalesItemList(List<SalesItemTally> salesItemList) {
		this.salesItemList = salesItemList;
	}
	
	public TallyConfigurationDTO getTallyConfig() {
		return tallyConfig;
	}
	public void setTallyConfig(TallyConfigurationDTO tallyConfig) {
		this.tallyConfig = tallyConfig;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public List<GstLedgerDTO> getGstLedgers() {
		return gstLedgers;
	}
	public void setGstLedgers(List<GstLedgerDTO> gstLedgers) {
		this.gstLedgers = gstLedgers;
	}
	
	public VoucherType getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}
	@Override
	public String toString() {
		return "SalesOrderTally [date=" + date + ", trimChar=" + trimChar + ", ledgerName=" + ledgerName
				+ ", ledgerAddress=" + ledgerAddress + ", priceLevelName=" + priceLevelName + ", documentTotal="
				+ documentTotal + ", remarks=" + remarks + ", pid=" + pid + ", employeeName=" + employeeName
				+ ", salesItemList=" + salesItemList + ", gstLedgers=" + gstLedgers + ", tallyConfig=" + tallyConfig
				+ "]";
	}
	
	
	
	
}
