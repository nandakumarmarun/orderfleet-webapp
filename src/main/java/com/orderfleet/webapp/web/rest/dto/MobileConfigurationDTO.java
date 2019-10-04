package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.MobileConfiguration;
import com.orderfleet.webapp.domain.enums.CartType;
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
	private boolean kfcEnabled;
	private boolean gpsMandatory;
	private boolean enableSecondarySales;

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
		this.kfcEnabled = mobileConfiguration.getKfcEnabled();
		this.gpsMandatory = mobileConfiguration.getGpsMandatory();
		this.enableSecondarySales = mobileConfiguration.getEnableSecondarySales();
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

}
