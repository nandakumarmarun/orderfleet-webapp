package com.orderfleet.webapp.web.rest.dto;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.MobileConfiguration;
import com.orderfleet.webapp.domain.enums.BluetoothPaperType;
import com.orderfleet.webapp.domain.enums.CartType;
import com.orderfleet.webapp.domain.enums.DisplayName;
import com.orderfleet.webapp.domain.enums.InventoryVoucherUIType;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;

/**
 * A DTO For MobileConfiguration.
 *
 * @author Sarath
 * @since Jul 17, 2017
 *
 */
public class MobileConfigurationDTO {

	private String pid;
	private String companyPid;
	private String companyName;
	private boolean taskExecutionSaveOfflineValue = true;
	private boolean promptAttendanceMarkingvalue;
	private boolean promptDayPlanUpdate;
	private boolean promptMasterDataUpdate;
	private boolean attendanceMarkingRequired;
	private boolean dayPlanDownloadRequired;
	private boolean masterDataUpdateRequired;
	private boolean buildDueDetail;
	private boolean includeAddressInAccountList;
	private boolean showAllActivityCount;
	private boolean createTerritory;
	private boolean realTimeProductPriceEnabled;
	private boolean hasGeoTag;
	private boolean hasPostDatedVoucher;
	private boolean promptVehicleMaster;
	private boolean addNewCustomer;
	private boolean preventNegativeStock;
	private VoucherNumberGenerationType voucherNumberGenerationType;
	private InventoryVoucherUIType inventoryVoucherUIType;
	private CartType cartType;
	private DisplayName accountProfileDisplayName;
	private DisplayName accountProfilePrintName;
	private DisplayName productProfileDisplayName;
	private DisplayName productProfilePrintName;
	private BluetoothPaperType bluetoothPaperType;
	private boolean kfcEnabled;
	private boolean gpsMandatory;
	private boolean enableSecondarySales;
	private boolean enableAttendanceImage;
	private boolean smartSearch;
	private boolean salesOrderDownloadPdf;
	private boolean findLocation;
	private boolean enableDynamicUnit;
	private boolean enableDiscountRoundOffColumn;
	private boolean stockLocationProducts;
	private boolean salesOrderAllocation;
	private boolean rateWithoutCalculation;
	private boolean showBestPerformerUpload;
	private boolean belowPriceLevel;
	private boolean amountToThreeDecimal;
	private boolean enableGeoFencing;
	private boolean receiptAllocationMandatory;
	private boolean blockActivity;
	private boolean salesOrderMandatory;
	private boolean rateWithTax;
	private boolean wifiPrintEnabler;
	private boolean blockCustomerByCredits;
	private boolean maxCartNos;
	private double  cartMaxSize;
	private boolean roundOffAutomation;
	private boolean ptenQuotationLayout;
	private boolean showDistanceFare;
 
	public MobileConfigurationDTO() {
		super();
	}

	public MobileConfigurationDTO(MobileConfiguration mobileConfiguration) {
		super();
		this.pid = mobileConfiguration.getPid();
		this.companyPid = mobileConfiguration.getCompany().getPid();
		this.companyName = mobileConfiguration.getCompany().getLegalName();
		this.taskExecutionSaveOfflineValue = mobileConfiguration.getTaskExecutionOfflineSave();
		this.promptAttendanceMarkingvalue = mobileConfiguration.getPromptAttendanceMarking();
		this.promptDayPlanUpdate = mobileConfiguration.getPromptDayPlanUpdate();
		this.promptMasterDataUpdate = mobileConfiguration.getPromptMasterdataUpdate();
		this.attendanceMarkingRequired = mobileConfiguration.getAttendanceMarkingRequired();
		this.dayPlanDownloadRequired = mobileConfiguration.getDayPlanDownloadRequired();
		this.masterDataUpdateRequired = mobileConfiguration.getMasterDataUpdateRequired();
		this.buildDueDetail = mobileConfiguration.getBuildDueDetails();
		this.includeAddressInAccountList = mobileConfiguration.getIncludeAddressInAccountlist();
		this.showAllActivityCount = mobileConfiguration.getShowAllActivityCount();
		this.createTerritory = mobileConfiguration.getCreateTerritory();
		this.realTimeProductPriceEnabled = mobileConfiguration.getRealTimeProductPriceEnabled();
		this.hasGeoTag = mobileConfiguration.getHasGeoTag();
		this.hasPostDatedVoucher = mobileConfiguration.getHasPostDatedVoucherEnabled();
		this.promptVehicleMaster = mobileConfiguration.getPromptVehicleMaster();
		this.addNewCustomer = mobileConfiguration.getAddNewCustomer();
		this.preventNegativeStock = mobileConfiguration.getPreventNegativeStock();
		this.voucherNumberGenerationType = mobileConfiguration.getVoucherNumberGenerationType();
		this.inventoryVoucherUIType = mobileConfiguration.getInventoryVoucherUIType();
		this.cartType = mobileConfiguration.getCartType();
		this.accountProfileDisplayName = mobileConfiguration.getAccountProfileDisplayName();
		this.accountProfilePrintName = mobileConfiguration.getAccountProfilePrintName();
		this.productProfileDisplayName = mobileConfiguration.getProductProfileDisplayName();
		this.productProfilePrintName = mobileConfiguration.getProductProfilePrintName();
		this.kfcEnabled = mobileConfiguration.getKfcEnabled();
		this.gpsMandatory = mobileConfiguration.getGpsMandatory();
		this.enableSecondarySales = mobileConfiguration.getEnableSecondarySales();
		this.enableAttendanceImage = mobileConfiguration.getEnableAttendanceImage();
		this.smartSearch = mobileConfiguration.getSmartSearch();
		this.salesOrderDownloadPdf = mobileConfiguration.getSalesOrderDownloadPdf();
		this.findLocation = mobileConfiguration.getFindLocation();
		this.enableDynamicUnit = mobileConfiguration.getEnableDynamicUnit();
		this.enableDiscountRoundOffColumn = mobileConfiguration.getEnableDiscountRoundOffColumn();
		this.stockLocationProducts = mobileConfiguration.getStockLocationProducts();
		this.salesOrderAllocation = mobileConfiguration.getSalesOrderAllocation();
		this.rateWithoutCalculation = mobileConfiguration.getRateWithoutCalculation();
		this.showBestPerformerUpload = mobileConfiguration.isShowBestPerformerUpload();
		this.belowPriceLevel = mobileConfiguration.isBelowPriceLevel();
		this.amountToThreeDecimal = mobileConfiguration.getAmountToThreeDecimal();
		this.enableGeoFencing = mobileConfiguration.getEnableGeoFencing();
		this.receiptAllocationMandatory = mobileConfiguration.getReceiptAllocationMandatory();
		this.blockActivity = mobileConfiguration.getBlockActivity();
		this.salesOrderMandatory = mobileConfiguration.getSalesOrderMandatory();
		this.rateWithTax = mobileConfiguration.getRateWithTax();
		this.wifiPrintEnabler = mobileConfiguration.getWifiPrintEnabler();
		this.blockCustomerByCredits = mobileConfiguration.isBlockCustomerByCredits();
        this.maxCartNos =mobileConfiguration.getMaxCartNos();
        this.cartMaxSize=mobileConfiguration.getCartMaxSize();
        this.roundOffAutomation=mobileConfiguration.isRoundOffAutomation();
        this.ptenQuotationLayout =mobileConfiguration.isPtenQuotationLayout();
        this.showDistanceFare = mobileConfiguration.getShowDistanceFare();
        this.bluetoothPaperType = mobileConfiguration.getBluetoothPaperType();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public boolean getTaskExecutionSaveOfflineValue() {
		return taskExecutionSaveOfflineValue;
	}

	public void setTaskExecutionSaveOfflineValue(boolean taskExecutionSaveOfflineValue) {
		this.taskExecutionSaveOfflineValue = taskExecutionSaveOfflineValue;
	}

	public boolean getPromptAttendanceMarkingvalue() {
		return promptAttendanceMarkingvalue;
	}

	public void setPromptAttendanceMarkingvalue(boolean promptAttendanceMarkingvalue) {
		this.promptAttendanceMarkingvalue = promptAttendanceMarkingvalue;
	}

	public boolean getPromptDayPlanUpdate() {
		return promptDayPlanUpdate;
	}

	public void setPromptDayPlanUpdate(boolean promptDayPlanUpdate) {
		this.promptDayPlanUpdate = promptDayPlanUpdate;
	}

	public boolean getPromptMasterDataUpdate() {
		return promptMasterDataUpdate;
	}

	public void setPromptMasterDataUpdate(boolean promptMasterDataUpdate) {
		this.promptMasterDataUpdate = promptMasterDataUpdate;
	}

	public boolean getAttendanceMarkingRequired() {
		return attendanceMarkingRequired;
	}

	public void setAttendanceMarkingRequired(boolean attendanceMarkingRequired) {
		this.attendanceMarkingRequired = attendanceMarkingRequired;
	}

	public boolean getDayPlanDownloadRequired() {
		return dayPlanDownloadRequired;
	}

	public void setDayPlanDownloadRequired(boolean dayPlanDownloadRequired) {
		this.dayPlanDownloadRequired = dayPlanDownloadRequired;
	}

	public boolean getMasterDataUpdateRequired() {
		return masterDataUpdateRequired;
	}

	public void setMasterDataUpdateRequired(boolean masterDataUpdateRequired) {
		this.masterDataUpdateRequired = masterDataUpdateRequired;
	}

	public boolean getBuildDueDetail() {
		return buildDueDetail;
	}

	public void setBuildDueDetail(boolean buildDueDetail) {
		this.buildDueDetail = buildDueDetail;
	}

	public boolean getIncludeAddressInAccountList() {
		return includeAddressInAccountList;
	}

	public void setIncludeAddressInAccountList(boolean includeAddressInAccountList) {
		this.includeAddressInAccountList = includeAddressInAccountList;
	}

	public boolean getShowAllActivityCount() {
		return showAllActivityCount;
	}

	public void setShowAllActivityCount(boolean showAllActivityCount) {
		this.showAllActivityCount = showAllActivityCount;
	}

	public boolean getCreateTerritory() {
		return createTerritory;
	}

	public void setCreateTerritory(boolean createTerritory) {
		this.createTerritory = createTerritory;
	}

	public boolean getRealTimeProductPriceEnabled() {
		return realTimeProductPriceEnabled;
	}

	public void setRealTimeProductPriceEnabled(boolean realTimeProductPriceEnabled) {
		this.realTimeProductPriceEnabled = realTimeProductPriceEnabled;
	}

	public boolean getHasGeoTag() {
		return hasGeoTag;
	}

	public void setHasGeoTag(boolean hasGeoTag) {
		this.hasGeoTag = hasGeoTag;
	}

	public boolean getHasPostDatedVoucher() {
		return hasPostDatedVoucher;
	}

	public void setHasPostDatedVoucher(boolean hasPostDatedVoucher) {
		this.hasPostDatedVoucher = hasPostDatedVoucher;
	}

	public boolean getPromptVehicleMaster() {
		return promptVehicleMaster;
	}

	public void setPromptVehicleMaster(boolean promptVehicleMaster) {
		this.promptVehicleMaster = promptVehicleMaster;
	}

	public boolean getAddNewCustomer() {
		return addNewCustomer;
	}

	public void setAddNewCustomer(boolean addNewCustomer) {
		this.addNewCustomer = addNewCustomer;
	}

	public boolean getPreventNegativeStock() {
		return preventNegativeStock;
	}

	public void setPreventNegativeStock(boolean preventNegativeStock) {
		this.preventNegativeStock = preventNegativeStock;
	}

	public VoucherNumberGenerationType getVoucherNumberGenerationType() {
		return voucherNumberGenerationType;
	}

	public void setVoucherNumberGenerationType(VoucherNumberGenerationType voucherNumberGenerationType) {
		this.voucherNumberGenerationType = voucherNumberGenerationType;
	}

	public InventoryVoucherUIType getInventoryVoucherUIType() {
		return inventoryVoucherUIType;
	}

	public void setInventoryVoucherUIType(InventoryVoucherUIType inventoryVoucherUIType) {
		this.inventoryVoucherUIType = inventoryVoucherUIType;
	}

	public CartType getCartType() {
		return cartType;
	}

	public void setCartType(CartType cartType) {
		this.cartType = cartType;
	}

	public boolean getKfcEnabled() {
		return kfcEnabled;
	}

	public void setKfcEnabled(boolean kfcEnabled) {
		this.kfcEnabled = kfcEnabled;
	}

	public boolean getGpsMandatory() {
		return gpsMandatory;
	}

	public void setGpsMandatory(boolean gpsMandatory) {
		this.gpsMandatory = gpsMandatory;
	}

	public boolean getEnableSecondarySales() {
		return enableSecondarySales;
	}

	public void setEnableSecondarySales(boolean enableSecondarySales) {
		this.enableSecondarySales = enableSecondarySales;
	}

	public boolean getEnableAttendanceImage() {
		return enableAttendanceImage;
	}

	public void setEnableAttendanceImage(boolean enableAttendanceImage) {
		this.enableAttendanceImage = enableAttendanceImage;
	}

	public boolean getSmartSearch() {
		return smartSearch;
	}

	public void setSmartSearch(boolean smartSearch) {
		this.smartSearch = smartSearch;
	}

	public boolean getSalesOrderDownloadPdf() {
		return salesOrderDownloadPdf;
	}

	public void setSalesOrderDownloadPdf(boolean salesOrderDownloadPdf) {
		this.salesOrderDownloadPdf = salesOrderDownloadPdf;
	}

	public boolean getFindLocation() {
		return findLocation;
	}

	public void setFindLocation(boolean findLocation) {
		this.findLocation = findLocation;
	}

	public boolean getEnableDynamicUnit() {
		return enableDynamicUnit;
	}

	public void setEnableDynamicUnit(boolean enableDynamicUnit) {
		this.enableDynamicUnit = enableDynamicUnit;
	}

	public boolean getEnableDiscountRoundOffColumn() {
		return enableDiscountRoundOffColumn;
	}

	public void setEnableDiscountRoundOffColumn(boolean enableDiscountRoundOffColumn) {
		this.enableDiscountRoundOffColumn = enableDiscountRoundOffColumn;
	}

	public boolean getStockLocationProducts() {
		return stockLocationProducts;
	}

	public void setStockLocationProducts(boolean stockLocationProducts) {
		this.stockLocationProducts = stockLocationProducts;
	}

	public boolean getSalesOrderAllocation() {
		return salesOrderAllocation;
	}

	public void setSalesOrderAllocation(boolean salesOrderAllocation) {
		this.salesOrderAllocation = salesOrderAllocation;
	}

	public boolean getRateWithoutCalculation() {
		return rateWithoutCalculation;
	}

	public void setRateWithoutCalculation(boolean rateWithoutCalculation) {
		this.rateWithoutCalculation = rateWithoutCalculation;
	}

	public boolean isShowBestPerformerUpload() {
		return showBestPerformerUpload;
	}

	public void setShowBestPerformerUpload(boolean showBestPerformerUpload) {
		this.showBestPerformerUpload = showBestPerformerUpload;
	}

	public boolean isBelowPriceLevel() {
		return belowPriceLevel;
	}

	public void setBelowPriceLevel(boolean belowPriceLevel) {
		this.belowPriceLevel = belowPriceLevel;
	}

	public boolean getAmountToThreeDecimal() {
		return amountToThreeDecimal;
	}

	public void setAmountToThreeDecimal(boolean amountToThreeDecimal) {
		this.amountToThreeDecimal = amountToThreeDecimal;
	}

	public boolean getEnableGeoFencing() {
		return enableGeoFencing;
	}

	public void setEnableGeoFencing(boolean enableGeoFencing) {
		this.enableGeoFencing = enableGeoFencing;
	}

	public boolean getReceiptAllocationMandatory() {
		return receiptAllocationMandatory;
	}

	public void setReceiptAllocationMandatory(boolean receiptAllocationMandatory) {
		this.receiptAllocationMandatory = receiptAllocationMandatory;
	}

	public boolean getBlockActivity() {
		return blockActivity;
	}

	public void setBlockActivity(boolean blockActivity) {
		this.blockActivity = blockActivity;
	}

	public boolean getSalesOrderMandatory() {
		return salesOrderMandatory;
	}

	public void setSalesOrderMandatory(boolean salesOrderMandatory) {
		this.salesOrderMandatory = salesOrderMandatory;
	}

	public boolean getRateWithTax() {
		return rateWithTax;
	}

	public void setRateWithTax(boolean rateWithTax) {
		this.rateWithTax = rateWithTax;
	}

	public boolean getWifiPrintEnabler() {
		return wifiPrintEnabler;
	}

	public void setWifiPrintEnabler(boolean wifiPrintEnabler) {
		this.wifiPrintEnabler = wifiPrintEnabler;
	}

	public boolean isBlockCustomerByCredits() {
		return blockCustomerByCredits;
	}

	public void setBlockCustomerByCredits(boolean blockCustomerByCredits) {
		this.blockCustomerByCredits = blockCustomerByCredits;
	}

	public DisplayName getAccountProfileDisplayName() {
		return accountProfileDisplayName;
	}

	public void setAccountProfileDisplayName(DisplayName accountProfileDisplayName) {
		this.accountProfileDisplayName = accountProfileDisplayName;
	}

	public DisplayName getAccountProfilePrintName() {
		return accountProfilePrintName;
	}

	public void setAccountProfilePrintName(DisplayName accountProfilePrintName) {
		this.accountProfilePrintName = accountProfilePrintName;
	}

	public DisplayName getProductProfileDisplayName() {
		return productProfileDisplayName;
	}

	public void setProductProfileDisplayName(DisplayName productProfileDisplayName) {
		this.productProfileDisplayName = productProfileDisplayName;
	}

	public DisplayName getProductProfilePrintName() {
		return productProfilePrintName;
	}

	public void setProductProfilePrintName(DisplayName productProfilePrintName) {
		this.productProfilePrintName = productProfilePrintName;
	}

	public boolean isMaxCartNos() {
		return maxCartNos;
	}

	public void setMaxCartNos(boolean maxCartNos) {
		this.maxCartNos = maxCartNos;
	}

	public double getCartMaxSize() {
		return cartMaxSize;
	}

	public void setCartMaxSize(double cartMaxSize) {
		this.cartMaxSize = cartMaxSize;
	}

	public boolean isRoundOffAutomation() {
		return roundOffAutomation;
	}

	public void setRoundOffAutomation(boolean roundOffAutomation) {
		this.roundOffAutomation = roundOffAutomation;
	}

	public boolean isPtenQuotationLayout() {
		return ptenQuotationLayout;
	}

	public void setPtenQuotationLayout(boolean ptenQuotationLayout) {
		this.ptenQuotationLayout = ptenQuotationLayout;
	}

	public boolean getShowDistanceFare() {
		return showDistanceFare;
	}

	public void setShowDistanceFare(boolean showDistanceFare) {
		this.showDistanceFare = showDistanceFare;
	}

	public BluetoothPaperType getBluetoothPaperType() {
		return bluetoothPaperType;
	}

	public void setBluetoothPaperType(BluetoothPaperType bluetoothPaperType) {
		this.bluetoothPaperType = bluetoothPaperType;
	}

	
}
