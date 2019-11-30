package com.orderfleet.webapp.web.tally.dto;

import java.util.ArrayList;
import java.util.List;

import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.enums.PaymentMode;

public class ReceiptDTO {

	private String ledgerName;
	private String trimChar;
	private double amount;
	private String date;
	private String chequeNo;
	private String bankName;
	private String chequeDate;
	private String narrationMessage;
	private PaymentMode mode;
	private String pid;
	private String receiptVoucherType;
	private String particularsName;
	private TallyConfigurationDTO tallyConfig;
	private List<BillAllocationDTO> billAllocations;
	private String employeeName;

	public ReceiptDTO() {
		super();
	}

	public ReceiptDTO(AccountingVoucherDetail accountingVoucherDetail) {
		super();
		this.ledgerName = accountingVoucherDetail.getBy().getName();
		this.trimChar = accountingVoucherDetail.getBy().getTrimChar();
		this.amount = accountingVoucherDetail.getAmount();
		this.date = accountingVoucherDetail.getVoucherDate().toString();
		this.chequeNo = accountingVoucherDetail.getInstrumentNumber();
		this.bankName = accountingVoucherDetail.getBankName();
		this.chequeDate = accountingVoucherDetail.getInstrumentDate() == null ? ""
				: accountingVoucherDetail.getInstrumentDate().toString();
		this.mode = accountingVoucherDetail.getMode();
		this.pid = accountingVoucherDetail.getAccountingVoucherHeader().getPid();
		this.particularsName = accountingVoucherDetail.getBy().getName();
		this.narrationMessage = accountingVoucherDetail.getRemarks();
		this.employeeName = accountingVoucherDetail.getAccountingVoucherHeader().getEmployee().getName();
		List<BillAllocationDTO> billAllocationDTOs = new ArrayList<>();
		for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
				.getAccountingVoucherAllocations()) {
			BillAllocationDTO billAllocationDTO = new BillAllocationDTO();
			billAllocationDTO.setName(accountingVoucherAllocation.getReferenceDocumentNumber());
			billAllocationDTO.setAmount(accountingVoucherAllocation.getAmount());
			billAllocationDTOs.add(billAllocationDTO);
		}
		this.billAllocations = billAllocationDTOs;
	}

	/*
	 * public ReceiptDTO(AccountingVoucherAllocation accountingVoucherAllocation) {
	 * super(); this.ledgerName =
	 * accountingVoucherAllocation.getAccountingVoucherDetail().getBy().getName();
	 * this.amount = accountingVoucherAllocation.getAmount(); this.date =
	 * accountingVoucherAllocation.getAccountingVoucherDetail().getVoucherDate().
	 * toString(); this.chequeNo =
	 * accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentNumber(
	 * ); this.bankName =
	 * accountingVoucherAllocation.getAccountingVoucherDetail().getBankName();
	 * this.chequeDate = accountingVoucherAllocation.getAccountingVoucherDetail()
	 * .getInstrumentDate()==null?"":accountingVoucherAllocation.
	 * getAccountingVoucherDetail().getInstrumentDate().toString();
	 * this.narrationMessage = accountingVoucherAllocation.getRemarks(); this.mode =
	 * accountingVoucherAllocation.getMode(); this.pid =
	 * accountingVoucherAllocation.getAccountingVoucherDetail()
	 * .getAccountingVoucherHeader().getPid(); this.particularsName =
	 * accountingVoucherAllocation.getAccountingVoucherDetail().getBy().getName(); }
	 */

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getTrimChar() {
		return trimChar;
	}

	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
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

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getNarrationMessage() {
		return narrationMessage;
	}

	public void setNarrationMessage(String narrationMessage) {
		this.narrationMessage = narrationMessage;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getReceiptVoucherType() {
		return receiptVoucherType;
	}

	public void setReceiptVoucherType(String receiptVoucherType) {
		this.receiptVoucherType = receiptVoucherType;
	}

	public String getParticularsName() {
		return particularsName;
	}

	public void setParticularsName(String particularsName) {
		this.particularsName = particularsName;
	}

	public TallyConfigurationDTO getTallyConfig() {
		return tallyConfig;
	}

	public void setTallyConfig(TallyConfigurationDTO tallyConfig) {
		this.tallyConfig = tallyConfig;
	}

	public List<BillAllocationDTO> getBillAllocations() {
		return billAllocations;
	}

	public void setBillAllocations(List<BillAllocationDTO> billAllocations) {
		this.billAllocations = billAllocations;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	
	
	

}
