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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.CartType;
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
