package com.orderfleet.webapp.web.rest.dto;

/**
 * A DTO for the CompanyConfigurationDTO entity.
 *
 * @author Sarath
 * @since Jul 28, 2017
 *
 */
public class CompanyConfigDTO {

	private String companyName;
	private String companyPid;
	private boolean distanceTraveled;
	private boolean locationVariance;
	private boolean interimSave;//save is equal to send
	private boolean refreshProductGroupProduct;//for refreshing product - product group association
	private boolean stageChangeAccountingVoucher;//for changing stage even if document in accounting voucher type  for geologic
	private boolean newCustomerAlias;//devsoft if new customer is added alias will be filled with N_1, N_2 etc
	private boolean chatReply;//settings to reply for a firebase notification
	private boolean salesPdfDownload;//setting for download as pdf button in sales order

	public CompanyConfigDTO() {
		super();
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

	public boolean isDistanceTraveled() {
		return distanceTraveled;
	}

	public void setDistanceTraveled(boolean distanceTraveled) {
		this.distanceTraveled = distanceTraveled;
	}

	public boolean isLocationVariance() {
		return locationVariance;
	}

	public void setLocationVariance(boolean locationVariance) {
		this.locationVariance = locationVariance;
	}

	public boolean getInterimSave() {
		return interimSave;
	}

	public void setInterimSave(boolean interimSave) {
		this.interimSave = interimSave;
	}

	public boolean isRefreshProductGroupProduct() {
		return refreshProductGroupProduct;
	}

	public void setRefreshProductGroupProduct(boolean refreshProductGroupProduct) {
		this.refreshProductGroupProduct = refreshProductGroupProduct;
	}

	public boolean getStageChangeAccountingVoucher() {
		return stageChangeAccountingVoucher;
	}

	public void setStageChangeAccountingVoucher(boolean stageChangeAccountingVoucher) {
		this.stageChangeAccountingVoucher = stageChangeAccountingVoucher;
	}

	public boolean getNewCustomerAlias() {
		return newCustomerAlias;
	}

	public void setNewCustomerAlias(boolean newCustomerAlias) {
		this.newCustomerAlias = newCustomerAlias;
	}

	public boolean getChatReply() {
		return chatReply;
	}

	public void setChatReply(boolean chatReply) {
		this.chatReply = chatReply;
	}

	public boolean getSalesPdfDownload() {
		return salesPdfDownload;
	}

	public void setSalesPdfDownload(boolean salesPdfDownload) {
		this.salesPdfDownload = salesPdfDownload;
	}
	
	
	

}
