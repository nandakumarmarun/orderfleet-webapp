package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.DebitCredit;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;

public class FinancialClosingReportDTO implements Comparable<FinancialClosingReportDTO> {
	
	private String documentPid;
	
	private String documentName;
	
	private DocumentType documentType;
	
	private DebitCredit debitCredit;
	
	private PaymentMode paymentMode;
	
	private Long sortOrder;
	
	private double amount;
	
	@Override
	public int compareTo(FinancialClosingReportDTO other) {
		return this.sortOrder.compareTo(other.sortOrder);
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

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((debitCredit == null) ? 0 : debitCredit.hashCode());
		result = prime * result + ((documentName == null) ? 0 : documentName.hashCode());
		result = prime * result + ((documentPid == null) ? 0 : documentPid.hashCode());
		result = prime * result + ((documentType == null) ? 0 : documentType.hashCode());
		result = prime * result + ((paymentMode == null) ? 0 : paymentMode.hashCode());
		result = prime * result + ((sortOrder == null) ? 0 : sortOrder.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinancialClosingReportDTO other = (FinancialClosingReportDTO) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (debitCredit != other.debitCredit)
			return false;
		if (documentName == null) {
			if (other.documentName != null)
				return false;
		} else if (!documentName.equals(other.documentName))
			return false;
		if (documentPid == null) {
			if (other.documentPid != null)
				return false;
		} else if (!documentPid.equals(other.documentPid))
			return false;
		if (documentType != other.documentType)
			return false;
		if (paymentMode != other.paymentMode)
			return false;
		if (sortOrder == null) {
			if (other.sortOrder != null)
				return false;
		} else if (!sortOrder.equals(other.sortOrder))
			return false;
		return true;
	}

}
