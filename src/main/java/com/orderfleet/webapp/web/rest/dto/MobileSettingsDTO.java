package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.domain.enums.BluetoothPaperType;
import com.orderfleet.webapp.domain.enums.CartType;
import com.orderfleet.webapp.domain.enums.DisplayName;
import com.orderfleet.webapp.domain.enums.InventoryVoucherUIType;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * a mobile settings.
 *
 * @author Sarath
 * @since May 24, 2017
 *
 */
public class MobileSettingsDTO {

	List<UserFavouriteDocumentDTO> favouriteDocuments;

	List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigs;

	GuidedSellingConfigDTO guidedSellingConfig;

	List<ActivityNotificationDTO> activityNotificationDtos;

	private boolean taskExecutionOfflineStatus = true;

	private boolean promptAttendanceMarking;

	private boolean promptDayPlanUpdate;

	private boolean promptMasterDataUpdate;

	private boolean attendanceMarkingRequired;

	private boolean dayPlanDownloadRequired;

	private boolean masterDataUpdateRequired;

	private boolean includeAddressInAccountList;

	private boolean buildDueDetail;

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

	private boolean devaQuotationLayout;
     
     private boolean showDistanceFare;
     
     private boolean multipleProduct;
     
     private boolean showAccountBalanceInReceiptAmount;

	private boolean enableOutstandingAmountDeduction;

	private boolean enablePreviousOrderItems;

	private boolean enablelivetracking;
	
	private boolean receiptShareOption;
	private boolean myPlanSequence;

	private Integer masterDataAutoUpdation;
	private boolean liveRouting;



	public MobileSettingsDTO() {
		super();
	}

	public MobileSettingsDTO(MobileConfigurationDTO mobileConfigurationDTO) {
		super();
		this.taskExecutionOfflineStatus = mobileConfigurationDTO.getTaskExecutionSaveOfflineValue();
		this.promptAttendanceMarking = mobileConfigurationDTO.getPromptAttendanceMarkingvalue();
		this.promptDayPlanUpdate = mobileConfigurationDTO.getPromptDayPlanUpdate();
		this.promptMasterDataUpdate = mobileConfigurationDTO.getPromptMasterDataUpdate();
		this.attendanceMarkingRequired = mobileConfigurationDTO.getAttendanceMarkingRequired();
		this.dayPlanDownloadRequired = mobileConfigurationDTO.getDayPlanDownloadRequired();
		this.masterDataUpdateRequired = mobileConfigurationDTO.getMasterDataUpdateRequired();
		this.includeAddressInAccountList = mobileConfigurationDTO.getIncludeAddressInAccountList();
		this.buildDueDetail = mobileConfigurationDTO.getBuildDueDetail();
		this.showAllActivityCount = mobileConfigurationDTO.getShowAllActivityCount();
		this.createTerritory = mobileConfigurationDTO.getCreateTerritory();
		this.realTimeProductPriceEnabled = mobileConfigurationDTO.getRealTimeProductPriceEnabled();
		this.hasGeoTag = mobileConfigurationDTO.getHasGeoTag();
		this.hasPostDatedVoucher = mobileConfigurationDTO.getHasPostDatedVoucher();
		this.promptVehicleMaster = mobileConfigurationDTO.getPromptVehicleMaster();
		this.addNewCustomer = mobileConfigurationDTO.getAddNewCustomer();
		this.preventNegativeStock = mobileConfigurationDTO.getPreventNegativeStock();
		this.voucherNumberGenerationType = mobileConfigurationDTO.getVoucherNumberGenerationType();
		this.inventoryVoucherUIType = mobileConfigurationDTO.getInventoryVoucherUIType();
		this.cartType = mobileConfigurationDTO.getCartType();
		this.accountProfileDisplayName = mobileConfigurationDTO.getAccountProfileDisplayName();
		this.accountProfilePrintName = mobileConfigurationDTO.getAccountProfilePrintName();
		this.productProfileDisplayName = mobileConfigurationDTO.getProductProfileDisplayName();
		this.productProfilePrintName = mobileConfigurationDTO.getProductProfilePrintName();
		this.kfcEnabled = mobileConfigurationDTO.getKfcEnabled();
		this.gpsMandatory = mobileConfigurationDTO.getGpsMandatory();
		this.enableSecondarySales = mobileConfigurationDTO.getEnableSecondarySales();
		this.enableAttendanceImage = mobileConfigurationDTO.getEnableAttendanceImage();
		this.smartSearch = mobileConfigurationDTO.getSmartSearch();
		this.salesOrderDownloadPdf = mobileConfigurationDTO.getSalesOrderDownloadPdf();
		this.findLocation = mobileConfigurationDTO.getFindLocation();
		this.enableDynamicUnit = mobileConfigurationDTO.getEnableDynamicUnit();
		this.enableDiscountRoundOffColumn = mobileConfigurationDTO.getEnableDiscountRoundOffColumn();
		this.stockLocationProducts = mobileConfigurationDTO.getStockLocationProducts();
		this.salesOrderAllocation = mobileConfigurationDTO.getSalesOrderAllocation();
		this.rateWithoutCalculation = mobileConfigurationDTO.getRateWithoutCalculation();
		this.showBestPerformerUpload = mobileConfigurationDTO.isShowBestPerformerUpload();
		this.belowPriceLevel = mobileConfigurationDTO.isBelowPriceLevel();
		this.amountToThreeDecimal = mobileConfigurationDTO.getAmountToThreeDecimal();
		this.enableGeoFencing = mobileConfigurationDTO.getEnableGeoFencing();
		this.receiptAllocationMandatory = mobileConfigurationDTO.getReceiptAllocationMandatory();
		this.blockActivity = mobileConfigurationDTO.getBlockActivity();
		this.salesOrderMandatory = mobileConfigurationDTO.getSalesOrderMandatory();
		this.rateWithTax = mobileConfigurationDTO.getRateWithTax();
		this.wifiPrintEnabler = mobileConfigurationDTO.getWifiPrintEnabler();
		this.blockCustomerByCredits=mobileConfigurationDTO.isBlockCustomerByCredits();
		this.maxCartNos=mobileConfigurationDTO.isMaxCartNos();
		this.cartMaxSize=mobileConfigurationDTO.getCartMaxSize();
		this.roundOffAutomation=mobileConfigurationDTO.isRoundOffAutomation();
		this.ptenQuotationLayout=mobileConfigurationDTO.isPtenQuotationLayout();
		this.showDistanceFare = mobileConfigurationDTO.getShowDistanceFare();
		this.bluetoothPaperType = mobileConfigurationDTO.getBluetoothPaperType();
		this.multipleProduct = mobileConfigurationDTO.getMultipleProduct();
		this.showAccountBalanceInReceiptAmount =mobileConfigurationDTO.getShowAccountBalanceInReceiptAmount();
		this.enableOutstandingAmountDeduction=mobileConfigurationDTO.getEnableOutstandingAmountDeduction();
		this.enablePreviousOrderItems = mobileConfigurationDTO.getEnablePreviousOrderItems();
		this.enablelivetracking = mobileConfigurationDTO.getEnablelivetracking();
		this.receiptShareOption = mobileConfigurationDTO.getReceiptShareOption();
		this.myPlanSequence = mobileConfigurationDTO.getMyPlanSequence();
		this.masterDataAutoUpdation = mobileConfigurationDTO.getMasterDataAutoUpdation();
		this.devaQuotationLayout = mobileConfigurationDTO.isDevaQuotationLayout();

	}

	public boolean isDevaQuotationLayout() {
		return devaQuotationLayout;
	}

	public void setDevaQuotationLayout(boolean devaQuotationLayout) {
		this.devaQuotationLayout = devaQuotationLayout;
	}

	public MobileSettingsDTO(UserWiseMobileConfigurationDTO userWiseMobileConfigurationDTO) {
		this.liveRouting = userWiseMobileConfigurationDTO.isLiveRouting();
	}

	public List<UserFavouriteDocumentDTO> getFavouriteDocuments() {
		return favouriteDocuments;
	}

	public void setFavouriteDocuments(List<UserFavouriteDocumentDTO> favouriteDocuments) {
		this.favouriteDocuments = favouriteDocuments;
	}

	public List<DayPlanExecutionConfigDTO> getDayPlanExecutionConfigs() {
		return dayPlanExecutionConfigs;
	}

	public void setDayPlanExecutionConfigs(List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigs) {
		this.dayPlanExecutionConfigs = dayPlanExecutionConfigs;
	}

	public GuidedSellingConfigDTO getGuidedSellingConfig() {
		return guidedSellingConfig;
	}

	public void setGuidedSellingConfig(GuidedSellingConfigDTO guidedSellingConfig) {
		this.guidedSellingConfig = guidedSellingConfig;
	}

	public boolean getTaskExecutionOfflineStatus() {
		return taskExecutionOfflineStatus;
	}

	public void setTaskExecutionOfflineStatus(boolean taskExecutionOfflineStatus) {
		this.taskExecutionOfflineStatus = taskExecutionOfflineStatus;
	}

	public boolean isPromptAttendanceMarking() {
		return promptAttendanceMarking;
	}

	public void setPromptAttendanceMarking(boolean promptAttendanceMarking) {
		this.promptAttendanceMarking = promptAttendanceMarking;
	}

	public List<ActivityNotificationDTO> getActivityNotificationDtos() {
		return activityNotificationDtos;
	}

	public void setActivityNotificationDtos(List<ActivityNotificationDTO> activityNotificationDtos) {
		this.activityNotificationDtos = activityNotificationDtos;
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

	public boolean isIncludeAddressInAccountList() {
		return includeAddressInAccountList;
	}

	public void setIncludeAddressInAccountList(boolean includeAddressInAccountList) {
		this.includeAddressInAccountList = includeAddressInAccountList;
	}

	public boolean isBuildDueDetail() {
		return buildDueDetail;
	}

	public void setBuildDueDetail(boolean buildDueDetail) {
		this.buildDueDetail = buildDueDetail;
	}

	public boolean isShowAllActivityCount() {
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

	public boolean isHasPostDatedVoucher() {
		return hasPostDatedVoucher;
	}

	public void setHasPostDatedVoucher(boolean hasPostDatedVoucher) {
		this.hasPostDatedVoucher = hasPostDatedVoucher;
	}

	public boolean isPromptVehicleMaster() {
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

	public boolean isSalesOrderDownloadPdf() {
		return salesOrderDownloadPdf;
	}

	public void setSalesOrderDownloadPdf(boolean salesOrderDownloadPdf) {
		this.salesOrderDownloadPdf = salesOrderDownloadPdf;
	}

	public boolean isFindLocation() {
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

	public boolean isEnableDiscountRoundOffColumn() {
		return enableDiscountRoundOffColumn;
	}

	public void setEnableDiscountRoundOffColumn(boolean enableDiscountRoundOffColumn) {
		this.enableDiscountRoundOffColumn = enableDiscountRoundOffColumn;
	}

	public boolean isStockLocationProducts() {
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

	public boolean getMultipleProduct() {
		return multipleProduct;
	}

	public void setMultipleProduct(boolean multipleProduct) {
		this.multipleProduct = multipleProduct;
	}

	public boolean getShowAccountBalanceInReceiptAmount() {
		return showAccountBalanceInReceiptAmount;
	}

	public void setShowAccountBalanceInReceiptAmount(boolean showAccountBalanceInReceiptAmount) {
		this.showAccountBalanceInReceiptAmount = showAccountBalanceInReceiptAmount;
	}

	public boolean isEnableOutstandingAmountDeduction() {
		return enableOutstandingAmountDeduction;
	}

	public void setEnableOutstandingAmountDeduction(boolean enableOutstandingAmountDeduction) {
		this.enableOutstandingAmountDeduction = enableOutstandingAmountDeduction;
	}

	public boolean getEnablePreviousOrderItems() {
		return enablePreviousOrderItems;
	}

	public void setEnablePreviousOrderItems(boolean enablePreviousOrderItems) {
		this.enablePreviousOrderItems = enablePreviousOrderItems;
	}

	public boolean getEnablelivetracking() {
		return enablelivetracking;
	}

	public void setEnablelivetracking(boolean enablelivetracking) {
		this.enablelivetracking = enablelivetracking;
	}

	public boolean isReceiptShareOption() {
		return receiptShareOption;
	}

	public void setReceiptShareOption(boolean receiptShareOption) {
		this.receiptShareOption = receiptShareOption;
	}

	public boolean isMyPlanSequence() {
		return myPlanSequence;
	}

	public void setMyPlanSequence(boolean myPlanSequence) {
		this.myPlanSequence = myPlanSequence;
	}

	public int getMasterDataAutoUpdation() {
		return masterDataAutoUpdation;
	}

	public void setMasterDataAutoUpdation(Integer masterDataAutoUpdation) {
		this.masterDataAutoUpdation = masterDataAutoUpdation;
	}

	public boolean isLiveRouting() {
		return liveRouting;
	}

	public void setLiveRouting(boolean liveRouting) {
		this.liveRouting = liveRouting;
	}
}
