package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orderfleet.webapp.domain.NewlyEditedAccountProfile;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewlyEditedAccountProfileDTO {

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

	private String tinNo;

	private double closingBalance;

	private double defaultDiscountPercentage;

	private String trimChar;

	private DataSourceType dataSourceType;

	private String stateName;

	private String countryName;

	private String newlyEditedPid;

	@NotNull
	@Size(max = 255)
	private String newlyEditedName;

	@Size(max = 55)
	private String newlyEditedAlias;

	private String newlyEditedUserPid;

	private String newlyEditedUserName;

	private String newlyEditedAccountProfilePid;

	private String newlyEditedAccountProfileName;

	private String newlyEditedDescription;

	private String newlyEditedAccountTypePid;

	private String newlyEditedAccountTypeName;

	private String newlyEditedGstRegistrationType;

	@NotNull
	@Size(min = 1, max = 500)
	private String newlyEditedAddress;

	@NotNull
	@Size(min = 1, max = 255)
	private String newlyEditedCity;

	@Size(max = 255)
	private String newlyEditedLocation;

	@Column(name = "pin")
	private String newlyEditedPin;

	private BigDecimal newlyEditedLatitude;

	private BigDecimal newlyEditedLongitude;

	@Size(max = 20)
	private String newlyEditedPhone1;

	@Size(max = 20)
	private String newlyEditedPhone2;

	@Email
	@Size(max = 100)
	private String newlyEditedEmail1;

	@Email
	@Size(max = 100)
	private String newlyEditedEmail2;

	private String newlyEditedWhatsAppNo;

	private String newlyEditedDefaultPriceLevelPid;

	private String newlyEditedDefaultPriceLevelName;

	private AccountStatus newlyEditedAccountStatus;

	private boolean newlyEditedImportStatus;

	private Long newlyEditedCreditDays;

	private Long newlyEditedCreditLimit;

	private String newlyEditedContactPerson;

	private boolean newlyEditedActivated;

	private LocalDateTime newlyEditedLastModifiedDate;

	private LocalDateTime newlyEditedCreatedDate;

	private String newlyEditedLeadToCashStage;

	private String newlyEditedTinNo;

	private double newlyEditedClosingBalance;

	private double newlyEditedDefaultDiscountPercentage;

	private String newlyEditedTrimChar;

	private DataSourceType newlyEditedDataSourceType;

	private String newlyEditedStateName;

	private String newlyEditedCountryName;

	private String employeeName;

	public NewlyEditedAccountProfileDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewlyEditedAccountProfileDTO(NewlyEditedAccountProfile newlyEditedAccountProfile) {
		super();

		this.pid = newlyEditedAccountProfile.getAccountProfile().getPid();
		this.name = newlyEditedAccountProfile.getAccountProfile().getName();
		this.alias = newlyEditedAccountProfile.getAccountProfile().getAlias();
		this.userName = newlyEditedAccountProfile.getAccountProfile().getUser().getFirstName();
		this.description = newlyEditedAccountProfile.getAccountProfile().getDescription();
		this.accountTypeName = newlyEditedAccountProfile.getAccountProfile().getName();
		this.address = newlyEditedAccountProfile.getAccountProfile().getAddress();
		this.city = newlyEditedAccountProfile.getAccountProfile().getCity();
		this.pin = newlyEditedAccountProfile.getAccountProfile().getPin();
		this.phone1 = newlyEditedAccountProfile.getAccountProfile().getPhone1();
		this.phone2 = newlyEditedAccountProfile.getAccountProfile().getPhone2();
		this.email1 = newlyEditedAccountProfile.getAccountProfile().getEmail1();
		this.email2 = newlyEditedAccountProfile.getAccountProfile().getEmail2();
		this.whatsAppNo = newlyEditedAccountProfile.getAccountProfile().getWhatsAppNo();
		this.accountStatus = newlyEditedAccountProfile.getAccountProfile().getAccountStatus();
		this.creditDays = newlyEditedAccountProfile.getAccountProfile().getCreditDays();
		this.creditLimit = newlyEditedAccountProfile.getAccountProfile().getCreditLimit();
		this.contactPerson = newlyEditedAccountProfile.getAccountProfile().getContactPerson();
		this.lastModifiedDate = newlyEditedAccountProfile.getAccountProfile().getLastModifiedDate();
		this.createdDate = newlyEditedAccountProfile.getAccountProfile().getCreatedDate();
		this.defaultDiscountPercentage = getDefaultDiscountPercentage();
		this.dataSourceType = newlyEditedAccountProfile.getAccountProfile().getDataSourceType();

		this.newlyEditedPid = newlyEditedAccountProfile.getPid();
		this.newlyEditedName = newlyEditedAccountProfile.getName();
		this.newlyEditedAlias = newlyEditedAccountProfile.getAlias().equalsIgnoreCase(
				newlyEditedAccountProfile.getAccountProfile().getAlias()) ? "-" : newlyEditedAccountProfile.getAlias();
		this.newlyEditedUserName = newlyEditedAccountProfile.getUser().getFirstName();
		this.newlyEditedDescription = newlyEditedAccountProfile.getDescription()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getDescription()) ? "-"
						: newlyEditedAccountProfile.getDescription();
		this.newlyEditedAccountTypeName = newlyEditedAccountProfile.getName();
		this.newlyEditedAddress = newlyEditedAccountProfile.getAddress()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getAddress()) ? "-"
						: newlyEditedAccountProfile.getAddress();
		this.newlyEditedCity = newlyEditedAccountProfile.getCity().equalsIgnoreCase(
				newlyEditedAccountProfile.getAccountProfile().getCity()) ? "-" : newlyEditedAccountProfile.getCity();
		this.newlyEditedPin = newlyEditedAccountProfile.getPin().equalsIgnoreCase(
				newlyEditedAccountProfile.getAccountProfile().getPin()) ? "-" : newlyEditedAccountProfile.getPin();
		this.newlyEditedPhone1 = newlyEditedAccountProfile.getPhone1()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getPhone1()) ? "-"
						: newlyEditedAccountProfile.getPhone1();
		this.newlyEditedPhone2 = newlyEditedAccountProfile.getPhone2()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getPhone2()) ? "-"
						: newlyEditedAccountProfile.getPhone2();
		this.newlyEditedEmail1 = newlyEditedAccountProfile.getEmail1()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getEmail1()) ? "-"
						: newlyEditedAccountProfile.getEmail1();
		this.newlyEditedEmail2 = newlyEditedAccountProfile.getEmail2()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getEmail2()) ? "-"
						: newlyEditedAccountProfile.getEmail2();
		this.newlyEditedWhatsAppNo = newlyEditedAccountProfile.getWhatsAppNo()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getWhatsAppNo()) ? "-"
						: newlyEditedAccountProfile.getWhatsAppNo();
		this.newlyEditedAccountStatus = newlyEditedAccountProfile.getAccountStatus();
		this.newlyEditedCreditDays = newlyEditedAccountProfile.getCreditDays() == newlyEditedAccountProfile
				.getAccountProfile().getCreditDays() ? 0 : newlyEditedAccountProfile.getCreditDays();
		this.newlyEditedCreditLimit = newlyEditedAccountProfile.getCreditLimit() == newlyEditedAccountProfile
				.getAccountProfile().getCreditLimit() ? 0 : newlyEditedAccountProfile.getCreditLimit();
		this.newlyEditedContactPerson = newlyEditedAccountProfile.getContactPerson()
				.equalsIgnoreCase(newlyEditedAccountProfile.getAccountProfile().getContactPerson()) ? "-"
						: newlyEditedAccountProfile.getContactPerson();
		this.newlyEditedLastModifiedDate = newlyEditedAccountProfile.getLastModifiedDate();
		this.newlyEditedCreatedDate = newlyEditedAccountProfile.getCreatedDate();
		this.newlyEditedDefaultDiscountPercentage = newlyEditedAccountProfile
				.getDefaultDiscountPercentage() == newlyEditedAccountProfile.getAccountProfile()
						.getDefaultDiscountPercentage() ? 0.0
								: newlyEditedAccountProfile.getDefaultDiscountPercentage();
		this.newlyEditedDataSourceType = newlyEditedAccountProfile.getDataSourceType();
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getGstRegistrationType() {
		return gstRegistrationType;
	}

	public void setGstRegistrationType(String gstRegistrationType) {
		this.gstRegistrationType = gstRegistrationType;
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

	public boolean isImportStatus() {
		return importStatus;
	}

	public void setImportStatus(boolean importStatus) {
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

	public boolean isActivated() {
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
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

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getNewlyEditedPid() {
		return newlyEditedPid;
	}

	public void setNewlyEditedPid(String newlyEditedPid) {
		this.newlyEditedPid = newlyEditedPid;
	}

	public String getNewlyEditedName() {
		return newlyEditedName;
	}

	public void setNewlyEditedName(String newlyEditedName) {
		this.newlyEditedName = newlyEditedName;
	}

	public String getNewlyEditedAlias() {
		return newlyEditedAlias;
	}

	public void setNewlyEditedAlias(String newlyEditedAlias) {
		this.newlyEditedAlias = newlyEditedAlias;
	}

	public String getNewlyEditedUserPid() {
		return newlyEditedUserPid;
	}

	public void setNewlyEditedUserPid(String newlyEditedUserPid) {
		this.newlyEditedUserPid = newlyEditedUserPid;
	}

	public String getNewlyEditedUserName() {
		return newlyEditedUserName;
	}

	public void setNewlyEditedUserName(String newlyEditedUserName) {
		this.newlyEditedUserName = newlyEditedUserName;
	}

	public String getNewlyEditedAccountProfilePid() {
		return newlyEditedAccountProfilePid;
	}

	public void setNewlyEditedAccountProfilePid(String newlyEditedAccountProfilePid) {
		this.newlyEditedAccountProfilePid = newlyEditedAccountProfilePid;
	}

	public String getNewlyEditedAccountProfileName() {
		return newlyEditedAccountProfileName;
	}

	public void setNewlyEditedAccountProfileName(String newlyEditedAccountProfileName) {
		this.newlyEditedAccountProfileName = newlyEditedAccountProfileName;
	}

	public String getNewlyEditedDescription() {
		return newlyEditedDescription;
	}

	public void setNewlyEditedDescription(String newlyEditedDescription) {
		this.newlyEditedDescription = newlyEditedDescription;
	}

	public String getNewlyEditedAccountTypePid() {
		return newlyEditedAccountTypePid;
	}

	public void setNewlyEditedAccountTypePid(String newlyEditedAccountTypePid) {
		this.newlyEditedAccountTypePid = newlyEditedAccountTypePid;
	}

	public String getNewlyEditedAccountTypeName() {
		return newlyEditedAccountTypeName;
	}

	public void setNewlyEditedAccountTypeName(String newlyEditedAccountTypeName) {
		this.newlyEditedAccountTypeName = newlyEditedAccountTypeName;
	}

	public String getNewlyEditedGstRegistrationType() {
		return newlyEditedGstRegistrationType;
	}

	public void setNewlyEditedGstRegistrationType(String newlyEditedGstRegistrationType) {
		this.newlyEditedGstRegistrationType = newlyEditedGstRegistrationType;
	}

	public String getNewlyEditedAddress() {
		return newlyEditedAddress;
	}

	public void setNewlyEditedAddress(String newlyEditedAddress) {
		this.newlyEditedAddress = newlyEditedAddress;
	}

	public String getNewlyEditedCity() {
		return newlyEditedCity;
	}

	public void setNewlyEditedCity(String newlyEditedCity) {
		this.newlyEditedCity = newlyEditedCity;
	}

	public String getNewlyEditedLocation() {
		return newlyEditedLocation;
	}

	public void setNewlyEditedLocation(String newlyEditedLocation) {
		this.newlyEditedLocation = newlyEditedLocation;
	}

	public String getNewlyEditedPin() {
		return newlyEditedPin;
	}

	public void setNewlyEditedPin(String newlyEditedPin) {
		this.newlyEditedPin = newlyEditedPin;
	}

	public BigDecimal getNewlyEditedLatitude() {
		return newlyEditedLatitude;
	}

	public void setNewlyEditedLatitude(BigDecimal newlyEditedLatitude) {
		this.newlyEditedLatitude = newlyEditedLatitude;
	}

	public BigDecimal getNewlyEditedLongitude() {
		return newlyEditedLongitude;
	}

	public void setNewlyEditedLongitude(BigDecimal newlyEditedLongitude) {
		this.newlyEditedLongitude = newlyEditedLongitude;
	}

	public String getNewlyEditedPhone1() {
		return newlyEditedPhone1;
	}

	public void setNewlyEditedPhone1(String newlyEditedPhone1) {
		this.newlyEditedPhone1 = newlyEditedPhone1;
	}

	public String getNewlyEditedPhone2() {
		return newlyEditedPhone2;
	}

	public void setNewlyEditedPhone2(String newlyEditedPhone2) {
		this.newlyEditedPhone2 = newlyEditedPhone2;
	}

	public String getNewlyEditedEmail1() {
		return newlyEditedEmail1;
	}

	public void setNewlyEditedEmail1(String newlyEditedEmail1) {
		this.newlyEditedEmail1 = newlyEditedEmail1;
	}

	public String getNewlyEditedEmail2() {
		return newlyEditedEmail2;
	}

	public void setNewlyEditedEmail2(String newlyEditedEmail2) {
		this.newlyEditedEmail2 = newlyEditedEmail2;
	}

	public String getNewlyEditedWhatsAppNo() {
		return newlyEditedWhatsAppNo;
	}

	public void setNewlyEditedWhatsAppNo(String newlyEditedWhatsAppNo) {
		this.newlyEditedWhatsAppNo = newlyEditedWhatsAppNo;
	}

	public String getNewlyEditedDefaultPriceLevelPid() {
		return newlyEditedDefaultPriceLevelPid;
	}

	public void setNewlyEditedDefaultPriceLevelPid(String newlyEditedDefaultPriceLevelPid) {
		this.newlyEditedDefaultPriceLevelPid = newlyEditedDefaultPriceLevelPid;
	}

	public String getNewlyEditedDefaultPriceLevelName() {
		return newlyEditedDefaultPriceLevelName;
	}

	public void setNewlyEditedDefaultPriceLevelName(String newlyEditedDefaultPriceLevelName) {
		this.newlyEditedDefaultPriceLevelName = newlyEditedDefaultPriceLevelName;
	}

	public AccountStatus getNewlyEditedAccountStatus() {
		return newlyEditedAccountStatus;
	}

	public void setNewlyEditedAccountStatus(AccountStatus newlyEditedAccountStatus) {
		this.newlyEditedAccountStatus = newlyEditedAccountStatus;
	}

	public boolean isNewlyEditedImportStatus() {
		return newlyEditedImportStatus;
	}

	public void setNewlyEditedImportStatus(boolean newlyEditedImportStatus) {
		this.newlyEditedImportStatus = newlyEditedImportStatus;
	}

	public Long getNewlyEditedCreditDays() {
		return newlyEditedCreditDays;
	}

	public void setNewlyEditedCreditDays(Long newlyEditedCreditDays) {
		this.newlyEditedCreditDays = newlyEditedCreditDays;
	}

	public Long getNewlyEditedCreditLimit() {
		return newlyEditedCreditLimit;
	}

	public void setNewlyEditedCreditLimit(Long newlyEditedCreditLimit) {
		this.newlyEditedCreditLimit = newlyEditedCreditLimit;
	}

	public String getNewlyEditedContactPerson() {
		return newlyEditedContactPerson;
	}

	public void setNewlyEditedContactPerson(String newlyEditedContactPerson) {
		this.newlyEditedContactPerson = newlyEditedContactPerson;
	}

	public boolean isNewlyEditedActivated() {
		return newlyEditedActivated;
	}

	public void setNewlyEditedActivated(boolean newlyEditedActivated) {
		this.newlyEditedActivated = newlyEditedActivated;
	}

	public LocalDateTime getNewlyEditedLastModifiedDate() {
		return newlyEditedLastModifiedDate;
	}

	public void setNewlyEditedLastModifiedDate(LocalDateTime newlyEditedLastModifiedDate) {
		this.newlyEditedLastModifiedDate = newlyEditedLastModifiedDate;
	}

	public LocalDateTime getNewlyEditedCreatedDate() {
		return newlyEditedCreatedDate;
	}

	public void setNewlyEditedCreatedDate(LocalDateTime newlyEditedCreatedDate) {
		this.newlyEditedCreatedDate = newlyEditedCreatedDate;
	}

	public String getNewlyEditedLeadToCashStage() {
		return newlyEditedLeadToCashStage;
	}

	public void setNewlyEditedLeadToCashStage(String newlyEditedLeadToCashStage) {
		this.newlyEditedLeadToCashStage = newlyEditedLeadToCashStage;
	}

	public String getNewlyEditedTinNo() {
		return newlyEditedTinNo;
	}

	public void setNewlyEditedTinNo(String newlyEditedTinNo) {
		this.newlyEditedTinNo = newlyEditedTinNo;
	}

	public double getNewlyEditedClosingBalance() {
		return newlyEditedClosingBalance;
	}

	public void setNewlyEditedClosingBalance(double newlyEditedClosingBalance) {
		this.newlyEditedClosingBalance = newlyEditedClosingBalance;
	}

	public double getNewlyEditedDefaultDiscountPercentage() {
		return newlyEditedDefaultDiscountPercentage;
	}

	public void setNewlyEditedDefaultDiscountPercentage(double newlyEditedDefaultDiscountPercentage) {
		this.newlyEditedDefaultDiscountPercentage = newlyEditedDefaultDiscountPercentage;
	}

	public String getNewlyEditedTrimChar() {
		return newlyEditedTrimChar;
	}

	public void setNewlyEditedTrimChar(String newlyEditedTrimChar) {
		this.newlyEditedTrimChar = newlyEditedTrimChar;
	}

	public DataSourceType getNewlyEditedDataSourceType() {
		return newlyEditedDataSourceType;
	}

	public void setNewlyEditedDataSourceType(DataSourceType newlyEditedDataSourceType) {
		this.newlyEditedDataSourceType = newlyEditedDataSourceType;
	}

	public String getNewlyEditedStateName() {
		return newlyEditedStateName;
	}

	public void setNewlyEditedStateName(String newlyEditedStateName) {
		this.newlyEditedStateName = newlyEditedStateName;
	}

	public String getNewlyEditedCountryName() {
		return newlyEditedCountryName;
	}

	public void setNewlyEditedCountryName(String newlyEditedCountryName) {
		this.newlyEditedCountryName = newlyEditedCountryName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

}
