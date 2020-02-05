package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;

/**
 * A Account.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@Entity
@Table(name = "tbl_account_profile")
public class AccountProfile implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_account_profile_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_account_profile_id") })
	@GeneratedValue(generator = "seq_account_profile_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_profile_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	private User user;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;

	@Column(name = "description")
	private String description;

	@NotNull
	@Size(min = 1, max = 500)
	@Column(name = "address", length = 500, nullable = false)
	private String address;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "city", length = 255, nullable = false)
	private String city;

	@Size(max = 255)
	@Column(name = "location", length = 255)
	private String location;

	@Size(max = 255)
	@Column(name = "gst_registration_type", length = 255)
	private String gstRegistrationType;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_account_geolocation_file", joinColumns = {
			@JoinColumn(name = "account_profile_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "file_id", referencedColumnName = "id") })
	private Set<File> geoLocationFiles = new HashSet<>();

	@Column(name = "pin")
	private String pin;

	@Column(name = "latitude", precision = 10, scale = 8)
	private BigDecimal latitude;

	@Column(name = "longitude", precision = 11, scale = 8)
	private BigDecimal longitude;

	@Size(max = 20)
	@Column(name = "phone_1", length = 20)
	private String phone1;

	@Size(max = 20)
	@Column(name = "phone_2", length = 20)
	private String phone2;

	@Size(max = 20)
	@Column(name = "whats_app_no", length = 20)
	private String whatsAppNo;

	@Email
	@Size(max = 100)
	@Column(name = "email_1", length = 100)
	private String email1;

	@Email
	@Size(max = 100)
	@Column(name = "email_2", length = 100)
	private String email2;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "account_status", nullable = false)
	private AccountStatus accountStatus;

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@NotNull
	@ManyToOne
	private AccountType accountType;

	@ManyToOne
	@JoinColumn(name = "price_level_id")
	private PriceLevel defaultPriceLevel;

	@NotNull
	@ManyToOne
	private Company company;

	// tally status
	@NotNull
	@Column(name = "import_status", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE' ")
	private Boolean importStatus = false;

	@Column(name = "credit_days")
	private Long creditDays;

	@Column(name = "credit_limit")
	private Long creditLimit;

	@Column(name = "contact_person")
	private String contactPerson;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "data_source_type", nullable = false, columnDefinition = "character varying DEFAULT 'WEB'")
	private DataSourceType dataSourceType = DataSourceType.WEB;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@Column(name = "lead_to_cash_stage")
	private String leadToCashStage;

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	@Column(name = "tin_no")
	private String tinNo;

	@Column(name = "closing_balance", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double closingBalance;

	@Column(name = "default_discount_percentage", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double defaultDiscountPercentage;

	@Column(name = "trim_char")
	private String trimChar;

	@Column(name = "state_name")
	private String stateName;

	@Column(name = "country_name")
	private String countryName;

	@Enumerated(EnumType.STRING)
	@Column(name = "geo_tagging_type")
	private GeoTaggingType geoTaggingType;
	
	@Column(name = "geo_tagged_time")
	private LocalDateTime geoTaggedTime;
	
	@ManyToOne
	private User geoTaggedUser;
	
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

	public Set<File> getGeoLocationFiles() {
		return geoLocationFiles;
	}

	public void setGeoLocationFiles(Set<File> geoLocationFiles) {
		this.geoLocationFiles = geoLocationFiles;
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

	public String getWhatsAppNo() {
		return whatsAppNo;
	}

	public void setWhatsAppNo(String whatsAppNo) {
		this.whatsAppNo = whatsAppNo;
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

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public PriceLevel getDefaultPriceLevel() {
		return defaultPriceLevel;
	}

	public void setDefaultPriceLevel(PriceLevel defaultPriceLevel) {
		this.defaultPriceLevel = defaultPriceLevel;
	}

	public Boolean getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(Boolean importStatus) {
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

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getGstRegistrationType() {
		return gstRegistrationType;
	}

	public void setGstRegistrationType(String gstRegistrationType) {
		this.gstRegistrationType = gstRegistrationType;
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

	public User getGeoTaggedUser() {
		return geoTaggedUser;
	}

	public void setGeoTaggedUser(User geoTaggedUser) {
		this.geoTaggedUser = geoTaggedUser;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AccountProfile account = (AccountProfile) o;
		if (account.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, account.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
