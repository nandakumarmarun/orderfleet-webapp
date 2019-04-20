package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;

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

}
