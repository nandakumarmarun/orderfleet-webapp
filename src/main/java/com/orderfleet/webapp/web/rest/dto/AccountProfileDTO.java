package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.GeoTaggingStatus;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountProfileDTO {

	private Long alterId;

	private String pid;

	@NotNull
	@Size(max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String userPid;

	private String userName;

	private String description;

	private String accountTypePid;

	private String accountTypeName;

	private String gstRegistrationType;

	private double locationRadius;

	@NotNull
	@Size(min = 1, max = 500)
	private String address;

	@NotNull
	@Size(min = 1, max = 255)
	private String city;

	@Size(max = 255)
	private String location;

	@Column(name = "pin")
	private String pin;

	private BigDecimal latitude;

	private BigDecimal longitude;

	@Size(max = 20)
	private String phone1;

	@Size(max = 20)
	private String phone2;

	@Email
	@Size(max = 100)
	private String email1;

	@Email
	@Size(max = 100)
	private String email2;

	private String whatsAppNo;

	private String defaultPriceLevelPid;

	private String defaultPriceLevelName;

	private AccountStatus accountStatus;

	private boolean importStatus;

	private Long creditDays;

	private Long creditLimit;

	private String contactPerson;

	private boolean activated;

	private LocalDateTime lastModifiedDate;

	private LocalDateTime createdDate;

	private String leadToCashStage;

	private String tinNo;

	private double closingBalance;

	private double defaultDiscountPercentage;

	private String trimChar;

	// Only for InventoryVoucherTransaction
	private boolean hasDefaultAccountInventory;

	private boolean promptStockLocationInventory;

	private DataSourceType dataSourceType = DataSourceType.WEB;

	private String stateName;

	private String countryName;

	private String districtName;

	private GeoTaggingType geoTaggingType;

	private LocalDateTime geoTaggedTime;

	private String geoTaggedUserName;

	private String geoTaggedUserPid;

	private String geoTaggedUserLogin;

	private String customerId;

	private String AitrichCode;

	private Long countryId;

	private String customerCode;

	private Long stateId;

	private Long districtId;

	private String mailingName;
	
	private String employeeName;
	
	private LocalDate minVoucherDate;

	private List<String> answers;

	private GeoTaggingStatus geoTaggingStatus;

	public String getAitrichCode() {
		return AitrichCode;
	}

	public void setAitrichCode(String aitrichCode) {
		AitrichCode = aitrichCode;
	}

	public AccountProfileDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountProfileDTO(CustomAccountProfiles customAccount) {
		super();
		this.pid = customAccount.getPid();
		this.name = customAccount.getName();
		this.alias = customAccount.getAlias();
		this.userName = customAccount.getUserName();
		this.description = customAccount.getDescription();
		this.accountTypeName = customAccount.getAaccountTypeName();
		this.address = customAccount.getAddress();
		this.city = customAccount.getCity();
		this.location = customAccount.getLocation();
		this.pin = customAccount.getPin();
		this.latitude = customAccount.getLatitude();
		this.longitude = customAccount.getLongitude();
		this.phone1 = customAccount.getPhone1();
		this.phone2 = customAccount.getPhone2();
		this.email1 = customAccount.getEmail1();
		this.email2 = customAccount.getEmail2();
		this.whatsAppNo = customAccount.getWhatsAppNo();
		this.accountStatus = customAccount.getAccountStatus();
		this.importStatus = customAccount.getImportStatus();
		this.creditDays = customAccount.getCreditDays();
		this.creditLimit = customAccount.getCreditLimit();
		this.contactPerson = customAccount.getContactPerson();
		this.activated = customAccount.getActivated();
		this.lastModifiedDate = customAccount.getLastModifiedDate();
		this.createdDate = customAccount.getCreatedDate();
		this.leadToCashStage = customAccount.getLeadToCashStage();
		this.tinNo = customAccount.getTinNo();
		this.closingBalance = customAccount.getClosingBalance();
		this.defaultDiscountPercentage = getDefaultDiscountPercentage();
		this.trimChar = customAccount.getTrimChar();
//		this.hasDefaultAccountInventory = hasDefaultAccountInventory;
//		this.promptStockLocationInventory = promptStockLocationInventory;
		this.dataSourceType = customAccount.getDataSourceType();
		this.locationRadius = customAccount.getlocationRadius();
	}

	public AccountProfileDTO(AccountProfile accountProfile)
	{
		this.pid= accountProfile.getPid();
		this.name = accountProfile.getName();
		this.alias = accountProfile.getAlias();
		this.userName = accountProfile.getUser().getFirstName();
		this.description = accountProfile.getDescription();
		this.accountTypePid = accountProfile.getAccountType().getPid();
		this.accountTypeName = accountProfile.getAccountType().getName();
		this.address = accountProfile.getAddress();
		this.city = accountProfile.getCity();
		this.location = accountProfile.getLocation();
		this.pin = accountProfile.getPin();
		this.latitude = accountProfile.getLatitude();
		this.longitude = accountProfile.getLongitude();
		this.phone1 = accountProfile.getPhone1();
		this.phone2 = accountProfile.getPhone2();
		this.email1 = accountProfile.getEmail1();
		this.email2 = accountProfile.getEmail2();
	
		this.activated = accountProfile.getActivated();
		this.lastModifiedDate = accountProfile.getLastModifiedDate();
		this.createdDate = accountProfile.getCreatedDate();
		this.closingBalance = accountProfile.getClosingBalance();
		this.geoTaggingStatus = accountProfile.getGeoTaggingStatus();
	}

	public AccountProfileDTO(String name, GeoTaggingStatus geoTaggingStatus) {
		this.name = name;
		this.geoTaggingStatus = geoTaggingStatus;
	}

	public Long getAlterId() {
		return alterId;
	}

	public void setAlterId(Long alterId) {
		this.alterId = alterId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAccountTypePid() {
		return accountTypePid;
	}

	public void setAccountTypePid(String accountTypePid) {
		this.accountTypePid = accountTypePid;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getGstRegistrationType() {
		return gstRegistrationType;
	}

	public void setGstRegistrationType(String gstRegistrationType) {
		this.gstRegistrationType = gstRegistrationType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getMailingName() {
		return mailingName;
	}

	public void setMailingName(String mailingName) {
		this.mailingName = mailingName;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getWhatsAppNo() {
		return whatsAppNo;
	}

	public void setWhatsAppNo(String whatsAppNo) {
		this.whatsAppNo = whatsAppNo;
	}

	public String getDefaultPriceLevelPid() {
		return defaultPriceLevelPid;
	}

	public void setDefaultPriceLevelPid(String defaultPriceLevelPid) {
		this.defaultPriceLevelPid = defaultPriceLevelPid;
	}

	public String getDefaultPriceLevelName() {
		return defaultPriceLevelName;
	}

	public void setDefaultPriceLevelName(String defaultPriceLevelName) {
		this.defaultPriceLevelName = defaultPriceLevelName;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public boolean getIsImportStatus() {
		return importStatus;
	}

	public void setIsImportStatus(boolean importStatus) {
		this.importStatus = importStatus;
	}

	public Long getCreditDays() {
		return creditDays;
	}

	public void setCreditDays(Long creditDays) {
		this.creditDays = creditDays;
	}

	public Long getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(Long creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getTinNo() {
		return tinNo;
	}

	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public boolean isImportStatus() {
		return importStatus;
	}

	public void setImportStatus(boolean importStatus) {
		this.importStatus = importStatus;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public double getDefaultDiscountPercentage() {
		return defaultDiscountPercentage;
	}

	public void setDefaultDiscountPercentage(double defaultDiscountPercentage) {
		this.defaultDiscountPercentage = defaultDiscountPercentage;
	}

	public String getTrimChar() {
		return trimChar;
	}

	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
	}

	public String getLeadToCashStage() {
		return leadToCashStage;
	}

	public void setLeadToCashStage(String leadToCashStage) {
		this.leadToCashStage = leadToCashStage;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public boolean getHasDefaultAccountInventory() {
		return hasDefaultAccountInventory;
	}

	public void setHasDefaultAccountInventory(boolean hasDefaultAccountInventory) {
		this.hasDefaultAccountInventory = hasDefaultAccountInventory;
	}

	public boolean getPromptStockLocationInventory() {
		return promptStockLocationInventory;
	}

	public void setPromptStockLocationInventory(boolean promptStockLocationInventory) {
		this.promptStockLocationInventory = promptStockLocationInventory;
	}

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public GeoTaggingType getGeoTaggingType() {
		return geoTaggingType;
	}

	public void setGeoTaggingType(GeoTaggingType geoTaggingType) {
		this.geoTaggingType = geoTaggingType;
	}

	public LocalDateTime getGeoTaggedTime() {
		return geoTaggedTime;
	}

	public void setGeoTaggedTime(LocalDateTime geoTaggedTime) {
		this.geoTaggedTime = geoTaggedTime;
	}

	public String getGeoTaggedUserName() {
		return geoTaggedUserName;
	}

	public void setGeoTaggedUserName(String geoTaggedUserName) {
		this.geoTaggedUserName = geoTaggedUserName;
	}

	public String getGeoTaggedUserPid() {
		return geoTaggedUserPid;
	}

	public void setGeoTaggedUserPid(String geoTaggedUserPid) {
		this.geoTaggedUserPid = geoTaggedUserPid;
	}

	public String getGeoTaggedUserLogin() {
		return geoTaggedUserLogin;
	}

	public void setGeoTaggedUserLogin(String geoTaggedUserLogin) {
		this.geoTaggedUserLogin = geoTaggedUserLogin;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public double getLocationRadius() {
		return locationRadius;
	}

	public void setLocationRadius(double locationRadius) {
		this.locationRadius = locationRadius;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public LocalDate getMinVoucherDate() {
		return minVoucherDate;
	}

	public void setMinVoucherDate(LocalDate minVoucherDate) {
		this.minVoucherDate = minVoucherDate;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public GeoTaggingStatus getGeoTaggingStatus() {
		return geoTaggingStatus;
	}

	public void setGeoTaggingStatus(GeoTaggingStatus geoTaggingStatus) {
		this.geoTaggingStatus = geoTaggingStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountProfileDTO accountProfileDTO = (AccountProfileDTO) o;

		if (!Objects.equals(pid, accountProfileDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "AccountProfileDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", userPid=" + userPid
				+ ", userName=" + userName + ", description=" + description + ", accountTypePid=" + accountTypePid
				+ ", accountTypeName=" + accountTypeName + ", address=" + address + ", city=" + city + ", location="
				+ location + ", pin=" + pin + ", latitude=" + latitude + ", longitude=" + longitude + ", phone1="
				+ phone1 + ", phone2=" + phone2 + ", email1=" + email1 + ", email2=" + email2 + ", whatsAppNo="
				+ whatsAppNo + ", defaultPriceLevelPid=" + defaultPriceLevelPid + ", defaultPriceLevelName="
				+ defaultPriceLevelName + ", accountStatus=" + accountStatus + ", importStatus=" + importStatus
				+ ", creditDays=" + creditDays + ", creditLimit=" + creditLimit + ", contactPerson=" + contactPerson
				+ ", activated=" + activated + ", lastModifiedDate=" + lastModifiedDate + ", createdDate=" + createdDate
				+ ", leadToCashStage=" + leadToCashStage + ", tinNo=" + tinNo + ", closingBalance=" + closingBalance
				+ ", defaultDiscountPercentage=" + defaultDiscountPercentage + ", trimChar=" + trimChar
				+ ", hasDefaultAccountInventory=" + hasDefaultAccountInventory + ", promptStockLocationInventory="
				+ promptStockLocationInventory + ", dataSourceType=" + dataSourceType + "]";
	}

}
