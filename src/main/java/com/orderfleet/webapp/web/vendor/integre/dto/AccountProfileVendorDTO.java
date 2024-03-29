package com.orderfleet.webapp.web.vendor.integre.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;

public class AccountProfileVendorDTO {

	private String pid;

	@NotNull
	@Size(max = 255)
	private String name;

	@Size(max = 55)
	private String code;

	private String userPid;

	private String userName;

	private String description;

	private String accountTypePid;

	private String accountTypeName;

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

	public AccountProfileVendorDTO() {
		super();
	}

	public AccountProfileVendorDTO(AccountProfile accountProfile) {
		super();
		this.pid = accountProfile.getPid();
		this.name = accountProfile.getName();
		this.code = accountProfile.getAlias();
		this.userPid = accountProfile.getUser().getPid();
		this.userName = accountProfile.getUser().getFirstName();
		this.description = accountProfile.getDescription();
		//this.accountTypePid = accountProfile.getAccountType().getPid();
		//this.accountTypeName = accountProfile.getAccountType().getName();
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
		this.whatsAppNo = accountProfile.getWhatsAppNo();
		//this.defaultPriceLevelPid = accountProfile.getDefaultPriceLevel().getPid();
		//this.defaultPriceLevelName = accountProfile.getDefaultPriceLevel().getName();
		this.accountStatus = accountProfile.getAccountStatus();
		this.importStatus = accountProfile.getImportStatus();
		this.creditDays = accountProfile.getCreditDays();
		this.creditLimit = accountProfile.getCreditLimit();
		this.contactPerson = accountProfile.getContactPerson();
		this.activated = accountProfile.getActivated();
		this.lastModifiedDate = accountProfile.getLastModifiedDate();
		this.createdDate = accountProfile.getCreatedDate();
		this.leadToCashStage = accountProfile.getLeadToCashStage();
		this.tinNo = accountProfile.getTinNo();
		this.closingBalance = accountProfile.getClosingBalance();
		this.defaultDiscountPercentage = accountProfile.getDefaultDiscountPercentage();
		this.trimChar = accountProfile.getTrimChar();
//		this.hasDefaultAccountInventory = 
//		this.promptStockLocationInventory = 
		this.dataSourceType = accountProfile.getDataSourceType();
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getLeadToCashStage() {
		return leadToCashStage;
	}

	public void setLeadToCashStage(String leadToCashStage) {
		this.leadToCashStage = leadToCashStage;
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

	public boolean isHasDefaultAccountInventory() {
		return hasDefaultAccountInventory;
	}

	public void setHasDefaultAccountInventory(boolean hasDefaultAccountInventory) {
		this.hasDefaultAccountInventory = hasDefaultAccountInventory;
	}

	public boolean isPromptStockLocationInventory() {
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountProfileVendorDTO accountProfileVendorDTO = (AccountProfileVendorDTO) o;

		if (!Objects.equals(pid, accountProfileVendorDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}
}
