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
	private boolean interimSave;// save is equal to send
	private boolean refreshProductGroupProduct;// for refreshing product - product group association
	private boolean stageChangeAccountingVoucher;// for changing stage even if document in accounting voucher type for
													// geologic
	private boolean newCustomerAlias;// devsoft if new customer is added alias will be filled with N_1, N_2 etc
	private boolean chatReply;// settings to reply for a firebase notification
	private boolean salesPdfDownload;// setting for download as pdf button in sales order
	private boolean visitBasedTransaction;// setting for visit based transaction
	private boolean salesManagement;// setting for Sales Management
	private boolean receiptsManagement;// setting for Receipt Management
	private boolean salesEditEnabled;// setting for KFC
	private boolean gpsVarianceQuery; // setting for gpsvariance
	private boolean sendSalesOrderEmail;
	private boolean findLocation;// do we need to find location
	private boolean sendSalesOrderSap;// setting for sending sales order to sap
	private boolean piecesToQuantity; // set pices and rate to quantity and selling rate(SAP)
	private boolean sendSalesOrderOdoo; // setting for sending sales order to odoo
	private boolean sendTransactionsSapPravesh;
	private boolean addCompoundUnit;
	private boolean updateStockLocation;
	private boolean sendToOdoo;
	private boolean enableProductGroupTax;
	private boolean aliasToName;
	private boolean descriptionToName;
	private boolean stockApi;
	private boolean employeeCreateBtn;
	private boolean modernSpecialConfig;
	private boolean salesOrderStatus;
	private boolean updateReciept;
	private boolean sendToFocus;
	private boolean sendEmailAutomaticaly;
	private boolean outstandingDateSorting;
	private boolean crmEnable;
	private boolean EnableStockCalculations;

	private boolean enableOutStanding;
	private boolean kilometercalculationsenable;

	private boolean enableDistanceSlabCalc;

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

	public boolean getVisitBasedTransaction() {
		return visitBasedTransaction;
	}

	public void setVisitBasedTransaction(boolean visitBasedTransaction) {
		this.visitBasedTransaction = visitBasedTransaction;
	}

	public boolean getSalesManagement() {
		return salesManagement;
	}

	public void setSalesManagement(boolean salesManagement) {
		this.salesManagement = salesManagement;
	}

	public boolean isReceiptsManagement() {
		return receiptsManagement;
	}

	public void setReceiptsManagement(boolean receiptsManagement) {
		this.receiptsManagement = receiptsManagement;
	}

	public boolean getSalesEditEnabled() {
		return salesEditEnabled;
	}

	public void setSalesEditEnabled(boolean salesEditEnabled) {
		this.salesEditEnabled = salesEditEnabled;
	}

	public boolean getGpsVarianceQuery() {
		return gpsVarianceQuery;
	}

	public void setGpsVarianceQuery(boolean gpsVarianceQuery) {
		this.gpsVarianceQuery = gpsVarianceQuery;
	}

	public boolean getSendSalesOrderEmail() {
		return sendSalesOrderEmail;
	}

	public void setSendSalesOrderEmail(boolean sendSalesOrderEmail) {
		this.sendSalesOrderEmail = sendSalesOrderEmail;
	}

	public boolean getFindLocation() {
		return findLocation;
	}

	public void setFindLocation(boolean findLocation) {
		this.findLocation = findLocation;
	}

	public boolean getSendSalesOrderSap() {
		return sendSalesOrderSap;
	}

	public void setSendSalesOrderSap(boolean sendSalesOrderSap) {
		this.sendSalesOrderSap = sendSalesOrderSap;
	}

	public boolean getPiecesToQuantity() {
		return piecesToQuantity;
	}

	public void setPiecesToQuantity(boolean piecesToQuantity) {
		this.piecesToQuantity = piecesToQuantity;
	}

	public boolean isSendSalesOrderOdoo() {
		return sendSalesOrderOdoo;
	}

	public void setSendSalesOrderOdoo(boolean sendSalesOrderOdoo) {
		this.sendSalesOrderOdoo = sendSalesOrderOdoo;
	}

	public boolean isSendTransactionsSapPravesh() {
		return sendTransactionsSapPravesh;
	}

	public void setSendTransactionsSapPravesh(boolean sendTransactionsSapPravesh) {
		this.sendTransactionsSapPravesh = sendTransactionsSapPravesh;
	}

	public boolean isAddCompoundUnit() {
		return addCompoundUnit;
	}

	public void setAddCompoundUnit(boolean addCompoundUnit) {
		this.addCompoundUnit = addCompoundUnit;
	}

	public boolean isUpdateStockLocation() {
		return updateStockLocation;
	}

	public void setUpdateStockLocation(boolean updateStockLocation) {
		this.updateStockLocation = updateStockLocation;
	}

	public boolean isSendToOdoo() {
		return sendToOdoo;
	}

	public void setSendToOdoo(boolean sendToOdoo) {
		this.sendToOdoo = sendToOdoo;
	}

	public boolean getEnableProductGroupTax() {
		return enableProductGroupTax;
	}

	public boolean getAliasToName() {
		return aliasToName;
	}

	public void setAliasToName(boolean aliasToName) {
		this.aliasToName = aliasToName;
	}

	public void setEnableProductGroupTax(boolean enableProductGroupTax) {
		this.enableProductGroupTax = enableProductGroupTax;
	}

	public boolean getDescriptionToName() {
		return descriptionToName;
	}

	public void setDescriptionToName(boolean descriptionToName) {
		this.descriptionToName = descriptionToName;
	}

	public boolean getStockApi() {
		return stockApi;
	}

	public void setStockApi(boolean stockApi) {
		this.stockApi = stockApi;
	}

	public boolean getEmployeeCreateBtn() {
		return employeeCreateBtn;
	}

	public void setEmployeeCreateBtn(boolean employeeCreateBtn) {
		this.employeeCreateBtn = employeeCreateBtn;
	}

	public boolean isModernSpecialConfig() {
		return modernSpecialConfig;
	}

	public void setModernSpecialConfig(boolean modernSpecialConfig) {
		this.modernSpecialConfig = modernSpecialConfig;
	}

	public boolean isSalesOrderStatus() {
		return salesOrderStatus;
	}

	public void setSalesOrderStatus(boolean salesOrderStatus) {
		this.salesOrderStatus = salesOrderStatus;
	}

	public boolean isUpdateReciept() {
		return updateReciept;
	}

	public void setUpdateReciept(boolean updateReciept) {
		this.updateReciept = updateReciept;
	}

	public boolean isSendToFocus() {
		return sendToFocus;
	}

	public void setSendToFocus(boolean sendToFocus) {
		this.sendToFocus = sendToFocus;
	}

	public boolean getSendEmailAutomaticaly() {
		return sendEmailAutomaticaly;
	}

	public void setSendEmailAutomaticaly(boolean sendEmailAutomaticaly) {
		this.sendEmailAutomaticaly = sendEmailAutomaticaly;
	}

	public boolean getCrmEnable() {
		return crmEnable;
	}

	public void setCrmEnable(boolean crmEnable) {
		this.crmEnable = crmEnable;
	}

	public boolean isOutstandingDateSorting() {
		return outstandingDateSorting;
	}

	public void setOutstandingDateSorting(boolean outstandingDateSorting) {
		this.outstandingDateSorting = outstandingDateSorting;
	}

	public boolean getEnableStockCalculations() {
		return EnableStockCalculations;
	}

	public void setEnableStockCalculations(boolean enableStockCalculations) {
		EnableStockCalculations = enableStockCalculations;
	}

	public boolean isEnableOutStanding() {
		return enableOutStanding;
	}

	public void setEnableOutStanding(boolean enableOutStanding) {
		this.enableOutStanding = enableOutStanding;
	}


	public boolean getKilometercalculationsenable() {
		return kilometercalculationsenable;
	}

	public void setKilometercalculationsenable(boolean kilometercalculationsenable) {
		this.kilometercalculationsenable = kilometercalculationsenable;
	}
	public boolean isEnableDistanceSlabCalc() {
		return enableDistanceSlabCalc;
	}

	public void setEnableDistanceSlabCalc(boolean enableDistanceSlabCalc) {
		this.enableDistanceSlabCalc = enableDistanceSlabCalc;
	}
}
