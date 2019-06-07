package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

import com.orderfleet.webapp.domain.enums.CompanyType;
import com.orderfleet.webapp.domain.enums.Industry;

/**
 * A Company.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Entity
@Table(name = "tbl_company")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Company extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_company_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_id") })
	@GeneratedValue(generator = "seq_company_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Pattern(regexp = "^[a-zA-Z ]*$")
	@Column(name = "legal_name", length = 150, unique = true, nullable = false, updatable = false)
	private String legalName;

	@Column(name = "alias", length = 30)
	private String alias;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "company_type", nullable = false)
	private CompanyType companyType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "industry", nullable = false)
	private Industry industry;

	@Column(name = "address_1")
	private String address1;

	@Column(name = "address_2")
	private String address2;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "country_id", nullable = false, updatable = false)
	private Country country;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "state_id", nullable = false, updatable = false)
	private State state;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "district_id", nullable = false, updatable = false)
	private District district;

	@Email
	@Size(max = 100)
	@Column(name = "email", length = 100, unique = true)
	private String email;

	@Size(max = 10)
	@Column(name = "pincode", length = 10)
	private String pincode;

	@NotNull
	@Column(name = "location", nullable = false)
	private String location;

	@Size(max = 5000000)
	@Lob
	@Column(name = "logo")
	private byte[] logo;

	@Column(name = "logo_content_type", length = 255)
	private String logoContentType;

	@Column(name = "website")
	private String website;

	@NotNull
	@Column(name = "activated", nullable = false)
	private Boolean activated;

	@Column(name = "version")
	@Version
	private Long version;

	@Column(name = "period_start_date")
	private LocalDate periodStartDate;

	@Column(name = "period_end_date")
	private LocalDate periodEndDate;

	@Column(name = "on_premise", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean onPremise;

	@Column(name = "api_url")
	private String apiUrl;

	@Column(name = "gst_number")
	private String gstNo;

	@Column(name = "sms_username")
	private String smsUsername;

	@Column(name = "sms_password")
	private String smsPassword;

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

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getAlias() {
		return alias;
	}

	public CompanyType getCompanyType() {
		return companyType;
	}

	public void setCompanyType(CompanyType companyType) {
		this.companyType = companyType;
	}

	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getLogoContentType() {
		return logoContentType;
	}

	public void setLogoContentType(String logoContentType) {
		this.logoContentType = logoContentType;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Boolean getIsActivated() {
		return activated;
	}

	public void setIsActivated(Boolean activated) {
		this.activated = activated;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public LocalDate getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(LocalDate periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public LocalDate getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(LocalDate periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public boolean isOnPremise() {
		return onPremise;
	}

	public void setOnPremise(boolean onPremise) {
		this.onPremise = onPremise;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public String getSmsUsername() {
		return smsUsername;
	}

	public void setSmsUsername(String smsUsername) {
		this.smsUsername = smsUsername;
	}

	public String getSmsPassword() {
		return smsPassword;
	}

	public void setSmsPassword(String smsPassword) {
		this.smsPassword = smsPassword;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Company company = (Company) o;
		if (company.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, company.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
