package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.BluetoothPaperType;
import com.orderfleet.webapp.domain.enums.CartType;
import com.orderfleet.webapp.domain.enums.DisplayName;
import com.orderfleet.webapp.domain.enums.InventoryVoucherUIType;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;

/**
 * A MobileConfiguration.
 *
 * @author Sarath
 * @since Jul 17, 2017
 *
 */
@Entity
@Table(name = "tbl_mobile_configuration")
public class MobileConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_mobile_configuration_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_mobile_configuration_id") })
	@GeneratedValue(generator = "seq_mobile_configuration_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_mobile_configuration_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "task_execution_offline_save", nullable = false)
	private boolean taskExecutionOfflineSave = true;

	@Column(name = "prompt_attendance_marking", nullable = false)
	private boolean promptAttendanceMarking;

	@Column(name = "prompt_day_plan_update", nullable = false)
	private boolean promptDayPlanUpdate;

	@Column(name = "prompt_masterdata_update", nullable = false)
	private boolean promptMasterdataUpdate;

	@Column(name = "attendance_marking_required", nullable = false)
	private boolean attendanceMarkingRequired;

	@Column(name = "day_plan_download_required", nullable = false)
	private boolean dayPlanDownloadRequired;

	@Column(name = "master_data_update_required", nullable = false)
	private boolean masterDataUpdateRequired;

	@Column(name = "build_due_details", nullable = false)
	private boolean buildDueDetails;

	@Column(name = "include_address_in_accountlist", nullable = false)
	private boolean includeAddressInAccountlist;

	@Column(name = "show_all_activity_count", nullable = false)
	private boolean showAllActivityCount;

	@Column(name = "create_territory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean createTerritory;

	@Column(name = "real_time_product_price_enabled", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean realTimeProductPriceEnabled;

	@Column(name = "has_geo_tag", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean hasGeoTag;

	@Column(name = "has_post_dated_voucher", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean hasPostDatedVoucherEnabled;

	@Column(name = "prompt_vehicle_master", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean promptVehicleMaster;

	@Column(name = "add_new_customer", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean addNewCustomer;

	@Column(name = "prevent_negative_stock", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean preventNegativeStock;

	@Column(name = "group_wise_cart", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean groupWiseCart;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "voucher_number_generation_type", nullable = false, columnDefinition = "character varying DEFAULT 'TYPE_1'")
	private VoucherNumberGenerationType voucherNumberGenerationType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "inventory_voucher_ui_type", nullable = false, columnDefinition = "character varying DEFAULT 'TYPE_1'")
	private InventoryVoucherUIType inventoryVoucherUIType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "cart_type", nullable = false, columnDefinition = "character varying DEFAULT 'NORMAL'")
	private CartType cartType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "account_profile_display_name", nullable = false, columnDefinition = "character varying DEFAULT 'NAME'")
	private DisplayName accountProfileDisplayName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "account_profile_print_name", nullable = false, columnDefinition = "character varying DEFAULT 'NAME'")
	private DisplayName accountProfilePrintName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "product_profile_display_name", nullable = false, columnDefinition = "character varying DEFAULT 'NAME'")
	private DisplayName productProfileDisplayName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "product_profile_print_name", nullable = false, columnDefinition = "character varying DEFAULT 'NAME'")
	private DisplayName productProfilePrintName;

	@Column(name = "kfc_enabled", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean kfcEnabled;

	@Column(name = "gps_mandatory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean gpsMandatory;

	@Column(name = "enable_secondory_sales", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableSecondarySales;

	@Column(name = "enable_attendance_image", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableAttendanceImage;

	@Column(name = "smart_search", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean smartSearch;

	@Column(name = "sales_order_download_pdf", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean salesOrderDownloadPdf;

	@Column(name = "find_location", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean findLocation;

	@Column(name = "enable_dynamic_unit", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableDynamicUnit;

	@Column(name = "enable_discount_round_off_column", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableDiscountRoundOffColumn;

	@Column(name = "sales_order_allocation", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean salesOrderAllocation;

	@Column(name = "rate_without_calculation", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean rateWithoutCalculation;

	@Column(name = "stock_location_products", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean stockLocationProducts;

	@Column(name = "show_best_performer_upload", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean showBestPerformerUpload;

	@Column(name = "below_price_level", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean belowPriceLevel;

	@Column(name = "amount_to_three_decimal", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean amountToThreeDecimal;

	@Column(name = "enable_geo_fencing", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableGeoFencing;

	@Column(name = "receipt_allocation_mandatory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean receiptAllocationMandatory;

	@Column(name = "block_activity", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean blockActivity;

	@Column(name = "sales_order_mandatory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean salesOrderMandatory;

	@Column(name = "rate_with_tax", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean rateWithTax;

	@Column(name = "wifi_Print_Enabler", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean wifiPrintEnabler;

	@Column(name = "block_Customer_By_Credits", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean blockCustomerByCredits;

	@Column(name = "max_Cart_Nos", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean maxCartNos;

	@Column(name = "cart_Max_Size", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double cartMaxSize;

	@Column(name = "round_Off_Automation", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean roundOffAutomation;

	@Column(name = "pTen_Quotation_Layout", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean ptenQuotationLayout;

	@Column(name = "show_Distance_Fare", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean showDistanceFare;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "bluetooth_peper_type", nullable = false, columnDefinition = "character varying DEFAULT 'SMALL'")
	private BluetoothPaperType bluetoothPaperType;
	
	@Column(name = "multiple_Product", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean multipleProduct;
	
	@Column(name = "show_AccountBalanceIn_ReceiptAmount", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean showAccountBalanceInReceiptAmount;


	@Column(name = "enable_outstanding_amount_deduction", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableOutstandingAmountDeduction;

	@Column(name = "enable_previous_order_items", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enablePreviousOrderItems;

	@Column(name = "enable_live_tracking", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enablelivetracking;

	@NotNull
	@ManyToOne
	private Company company;

	public MobileConfiguration() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public boolean getTaskExecutionOfflineSave() {
		return taskExecutionOfflineSave;
	}

	public void setTaskExecutionOfflineSave(boolean taskExecutionOfflineSave) {
		this.taskExecutionOfflineSave = taskExecutionOfflineSave;
	}

	public boolean getPromptAttendanceMarking() {
		return promptAttendanceMarking;
	}

	public void setPromptAttendanceMarking(boolean promptAttendanceMarking) {
		this.promptAttendanceMarking = promptAttendanceMarking;
	}

	public boolean getPromptDayPlanUpdate() {
		return promptDayPlanUpdate;
	}

	public void setPromptDayPlanUpdate(boolean promptDayPlanUpdate) {
		this.promptDayPlanUpdate = promptDayPlanUpdate;
	}

	public boolean getPromptMasterdataUpdate() {
		return promptMasterdataUpdate;
	}

	public void setPromptMasterdataUpdate(boolean promptMasterdataUpdate) {
		this.promptMasterdataUpdate = promptMasterdataUpdate;
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

	public boolean getBuildDueDetails() {
		return buildDueDetails;
	}

	public void setBuildDueDetails(boolean buildDueDetails) {
		this.buildDueDetails = buildDueDetails;
	}

	public boolean getIncludeAddressInAccountlist() {
		return includeAddressInAccountlist;
	}

	public void setIncludeAddressInAccountlist(boolean includeAddressInAccountlist) {
		this.includeAddressInAccountlist = includeAddressInAccountlist;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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

	public boolean getHasPostDatedVoucherEnabled() {
		return hasPostDatedVoucherEnabled;
	}

	public void setHasPostDatedVoucherEnabled(boolean hasPostDatedVoucherEnabled) {
		this.hasPostDatedVoucherEnabled = hasPostDatedVoucherEnabled;
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

	public boolean getGroupWiseCart() {
		return groupWiseCart;
	}

	public void setGroupWiseCart(boolean groupWiseCart) {
		this.groupWiseCart = groupWiseCart;
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

	public boolean getSmartSearch() {
		return smartSearch;
	}

	public void setSmartSearch(boolean smartSearch) {
		this.smartSearch = smartSearch;
	}

	public boolean getEnableAttendanceImage() {
		return enableAttendanceImage;
	}

	public void setEnableAttendanceImage(boolean enableAttendanceImage) {
		this.enableAttendanceImage = enableAttendanceImage;
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

	public boolean getMaxCartNos() {
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

	public boolean getEnableOutstandingAmountDeduction() {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MobileConfiguration mobileConfiguration = (MobileConfiguration) o;
		if (mobileConfiguration.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, mobileConfiguration.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
