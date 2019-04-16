package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.enums.DashboardUIType;
import com.orderfleet.webapp.domain.enums.DeviceType;

/**
 * A user.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Entity
@Table(name = "tbl_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_id") })
	@GeneratedValue(generator = "seq_user_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false, updatable = false)
	private Company company;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = true, nullable = false)
	private String login;

	@JsonIgnore
	@NotNull
	@Size(min = 4, max = 60)
	@Column(name = "password_hash", length = 60)
	private String password;

	@Size(max = 50)
	@Column(name = "first_name", length = 50)
	private String firstName;

	@Size(max = 50)
	@Column(name = "last_name", length = 50)
	private String lastName;

	@NotNull
	@Email
	@Size(max = 100)
	@Column(length = 100, unique = true, nullable = false)
	private String email;

	@NotNull
	//@Pattern(regexp = "(^$|[0-9]{10})")
	@Column(name = "mobile", length = 15, nullable = false)
	private String mobile;

	@NotNull
	@Column(nullable = false)
	private boolean activated = false;

	@Size(min = 2, max = 5)
	@Column(name = "lang_key", length = 5)
	private String langKey;

	@Size(max = 255)
	@Column(name = "device_key", length = 255)
	@JsonIgnore
	private String deviceKey;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "device_type", nullable = false)
	private DeviceType deviceType = DeviceType.TABLET;

	@Size(max = 20)
	@Column(name = "activation_key", length = 20)
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	private String resetKey;

	@Column(name = "reset_date", nullable = true)
	private ZonedDateTime resetDate = null;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "tbl_user_authority", joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "authority_name", referencedColumnName = "name") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@BatchSize(size = 20)
	private Set<Authority> authorities = new HashSet<>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<PersistentToken> persistentTokens = new HashSet<>();

	@Column(name = "version")
	@Version
	private Long version;
	
	@Column(name = "show_all_users_data", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean showAllUsersData = true;
	
	@Column(name = "enable_websocket", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean enableWebsocket = true;
	
	@Column(name = "chart_color")
	private String chartColor;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "dashboard_ui_type", nullable = false, columnDefinition = "varchar(10) default 'TW'")
	private DashboardUIType dashboardUIType;
	
	@Column(name = "discontinued", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean discontinued = false;
	
	@Column(name = "discontinued_mobile_completed_date")
	private LocalDateTime discontinuedMobileCompletedDate;

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public ZonedDateTime getResetDate() {
		return resetDate;
	}

	public void setResetDate(ZonedDateTime resetDate) {
		this.resetDate = resetDate;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Set<PersistentToken> getPersistentTokens() {
		return persistentTokens;
	}

	public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
		this.persistentTokens = persistentTokens;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public boolean getShowAllUsersData() {
		return showAllUsersData;
	}

	public void setShowAllUsersData(boolean showAllUsersData) {
		this.showAllUsersData = showAllUsersData;
	}

	public boolean getEnableWebsocket() {
		return enableWebsocket;
	}

	public void setEnableWebsocket(boolean enableWebsocket) {
		this.enableWebsocket = enableWebsocket;
	}
	

	public String getChartColor() {
		return chartColor;
	}

	public void setChartColor(String chartColor) {
		this.chartColor = chartColor;
	}

	public DashboardUIType getDashboardUIType() {
		return dashboardUIType;
	}

	public void setDashboardUIType(DashboardUIType dashboardUIType) {
		this.dashboardUIType = dashboardUIType;
	}
	

	public boolean getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(boolean discontinued) {
		this.discontinued = discontinued;
	}

	public LocalDateTime getDiscontinuedMobileCompletedDate() {
		return discontinuedMobileCompletedDate;
	}

	public void setDiscontinuedMobileCompletedDate(LocalDateTime discontinuedMobileCompletedDate) {
		this.discontinuedMobileCompletedDate = discontinuedMobileCompletedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return login.equals(user.login);
	}

	@Override
	public int hashCode() {
		return login.hashCode();
	}

	@Override
	public String toString() {
		return "User{" + "login='" + login + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName
				+ '\'' + ", email='" + email + '\'' + ", mobile='" + mobile + '\'' + ", activated='" + activated + '\''
				+ ", langKey='" + langKey + '\'' + ", deviceKey='" + deviceKey + '\'' + ", version='" + version + '\''
				+ "}";
	}
}
