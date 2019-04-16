package com.orderfleet.webapp.web.rest.dto;

/**
 * a DTO for sales value report.
 *
 * @author Sarath
 * @since Jun 9, 2017
 *
 */
public class SalesValueReportDTO {

	private String purchaseHistoryConfigName;

	private int purchaseHistoryConfigOrderNo;

	private String label;

	private String accountName;

	private String accountPid;

	private double documentTotal;

	public SalesValueReportDTO(String purchaseHistoryConfigName, int purchaseHistoryConfigOrderNo, String label,
			String accountName, String accountPid, double documentTotal) {
		super();
		this.purchaseHistoryConfigName = purchaseHistoryConfigName;
		this.purchaseHistoryConfigOrderNo = purchaseHistoryConfigOrderNo;
		this.label = label;
		this.accountName = accountName;
		this.accountPid = accountPid;
		this.documentTotal = documentTotal;
	}

	public String getPurchaseHistoryConfigName() {
		return purchaseHistoryConfigName;
	}

	public void setPurchaseHistoryConfigName(String purchaseHistoryConfigName) {
		this.purchaseHistoryConfigName = purchaseHistoryConfigName;
	}

	public int getPurchaseHistoryConfigOrderNo() {
		return purchaseHistoryConfigOrderNo;
	}

	public void setPurchaseHistoryConfigOrderNo(int purchaseHistoryConfigOrderNo) {
		this.purchaseHistoryConfigOrderNo = purchaseHistoryConfigOrderNo;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountPid() {
		return accountPid;
	}

	public void setAccountPid(String accountPid) {
		this.accountPid = accountPid;
	}

	public double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}

	@Override
	public String toString() {
		return "SalesValueReportDTO [purchaseHistoryConfigName=" + purchaseHistoryConfigName
				+ ", purchaseHistoryConfigOrderNo=" + purchaseHistoryConfigOrderNo + ", label=" + label
				+ ", accountName=" + accountName + ", accountPid=" + accountPid + ", documentTotal=" + documentTotal
				+ "]";
	}

}
