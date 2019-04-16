package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_registration_data")
public class RegistrationData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_registration_data_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_registration_data_id") })
	@GeneratedValue(generator = "seq_registration_data_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_registration_data_id')")
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "fullName", length = 255, nullable = false)
	private String fullName;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "companyName", length = 255, nullable = false)
	private String companyName;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "shortName", length = 255, nullable = false)
	private String shortName;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "country_id", nullable = false, updatable = false)
	private Country country;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "userCount")
	private int userCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	@Override
	public String toString() {
		return "RegistrationData [id=" + id + ", fullName=" + fullName + ", companyName=" + companyName + ", shortName="
				+ shortName + ", country=" + country + ", company=" + company + ", userCount=" + userCount + "]";
	}

}
