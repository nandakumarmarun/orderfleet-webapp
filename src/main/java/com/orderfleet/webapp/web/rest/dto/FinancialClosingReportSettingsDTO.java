package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.FinancialClosingReportSettings;
import com.orderfleet.webapp.domain.enums.DebitCredit;
import com.orderfleet.webapp.domain.enums.PaymentMode;

public class FinancialClosingReportSettingsDTO {
	
	private Long id;
	
	private String documentPid;
	
	private String documentName;
	
	private DebitCredit debitCredit;
	
	private PaymentMode paymentMode;
	
	private Long sortOrder;

	public FinancialClosingReportSettingsDTO() {
		super();
	}

	public FinancialClosingReportSettingsDTO(Long id, String documentPid, String documentName,
			DebitCredit debitCredit, PaymentMode paymentMode, Long sortOrder) {
		super();
		this.id = id;
		this.documentPid = documentPid;
		this.documentName = documentName;
		this.debitCredit = debitCredit;
		this.paymentMode = paymentMode;
		this.sortOrder = sortOrder;
	}
	
	public FinancialClosingReportSettingsDTO(FinancialClosingReportSettings salesReportSettings) {
		super();
		this.id = salesReportSettings.getId();
		this.documentPid = salesReportSettings.getDocument().getPid();
		this.documentName = salesReportSettings.getDocument().getName();
		this.debitCredit = salesReportSettings.getDebitCredit();
		this.paymentMode = salesReportSettings.getPaymentMode();
		this.sortOrder = salesReportSettings.getSortOrder();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public DebitCredit getDebitCredit() {
		return debitCredit;
	}

	public void setDebitCredit(DebitCredit debitCredit) {
		this.debitCredit = debitCredit;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}
}
