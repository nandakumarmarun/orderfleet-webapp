package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *Domain for AccountProfileGeoLocationTagging
 *
 * @author fahad
 * @since Jul 7, 2017
 */
@Entity
@Table(name = "tbl_account_profile_geo_location_tagging")
public class AccountProfileGeoLocationTagging implements Serializable{

	
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_account_profile_geo_location_tagging_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_account_profile_geo_location_tagging_id") })
	@GeneratedValue(generator = "seq_account_profile_geo_location_tagging_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_profile_geo_location_tagging_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@ManyToOne
	private User user;
	
	@NotNull
	@ManyToOne
	private AccountProfile accountProfile;
	
	
	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();
	
	@NotNull
	@Column(name = "send_date", nullable = false)
	private LocalDateTime sendDate;
	
	@Column(name = "latitude", precision = 10, scale = 8)
	private BigDecimal latitude;
	
	@Column(name = "longitude", precision = 10, scale = 8)
	private BigDecimal longitude;
	
	@Size(max = 255)
	@Column(name = "location", length = 255)
	private String location;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AccountProfileGeoLocationTagging accountProfileGeoLocation = (AccountProfileGeoLocationTagging) o;
		if (accountProfileGeoLocation.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, accountProfileGeoLocation.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "AccountProfileGeoLocationTagging [id=" + id + ", pid=" + pid + ", company=" + company + ", user=" + user
				+ ", accountProfile=" + accountProfile + ", createdDate=" + createdDate + ", sendDate=" + sendDate
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", location=" + location + "]";
	}
	
	
}
