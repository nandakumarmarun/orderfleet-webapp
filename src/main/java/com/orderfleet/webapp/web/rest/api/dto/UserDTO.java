package com.orderfleet.webapp.web.rest.api.dto;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DashboardUIType;

/**
 * A DTO representing a user, with his authorities.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
public class UserDTO {

	private String pid;

	// for tab to validate licence key
	private Long companyId;

	private String companyPid;

	private String companyName;

	private String gstNumber;

	private String companyPhoneNo;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	private String login;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@Email
	@Size(min = 5, max = 100)
	private String email;

	@NotNull
	// @Pattern(regexp="(^$|[0-9]{10})")
	private String mobile;

	private boolean activated = false;

	@Size(min = 2, max = 5)
	private String langKey;

	private String deviceKey;

	private Set<String> authorities;

	private boolean showAllUserData = true;

	private String chartColor;

	private DashboardUIType dashboardUIType;

	private boolean discontinued;

	private String employeeName;

	private String employeeAlias;

	public UserDTO() {
	}

	public UserDTO(User user) {
		this(user.getPid(), user.getCompany().getPid(), user.getCompany().getLegalName(), user.getLogin(),
				user.getFirstName(), user.getLastName(), user.getEmail(), user.getMobile(), user.getActivated(),
				user.getLangKey(), user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()),
				user.getShowAllUsersData(), user.getDashboardUIType(), user.getDiscontinued(), user.getDeviceKey(),
				user.getCompany().getGstNo(), user.getCompany().getPhoneNo());
	}

	public UserDTO(String pid, String companyPid, String companyName, String login, String firstName, String lastName,
			String email, String mobile, boolean activated, String langKey, Set<String> authorities,
			boolean showAllUserData, DashboardUIType dashboardUIType, boolean discontinued, String deviceKey,
			String gstNumber, String companyPhoneNo) {
		this.pid = pid;
		this.companyPid = companyPid;
		this.companyName = companyName;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.activated = activated;
		this.langKey = langKey;
		this.authorities = authorities;
		this.showAllUserData = showAllUserData;
		this.dashboardUIType = dashboardUIType;
		this.discontinued = discontinued;
		this.deviceKey = deviceKey;
		this.gstNumber = gstNumber;
		this.companyPhoneNo = companyPhoneNo;
	}

	public UserDTO(String pid, String firstName, String lastName) {
		this.pid = pid;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getLangKey() {
		return langKey;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public boolean isShowAllUserData() {
		return showAllUserData;
	}

	public void setShowAllUserData(boolean showAllUserData) {
		this.showAllUserData = showAllUserData;
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

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getCompanyPhoneNo() {
		return companyPhoneNo;
	}

	public void setCompanyPhoneNo(String companyPhoneNo) {
		this.companyPhoneNo = companyPhoneNo;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeAlias() {
		return employeeAlias;
	}

	public void setEmployeeAlias(String employeeAlias) {
		this.employeeAlias = employeeAlias;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserDTO user = (UserDTO) o;
		return login.equals(user.login);
	}

	@Override
	public int hashCode() {
		return login.hashCode();
	}

	@Override
	public String toString() {
		return "UserDTO [pid=" + pid + ", companyId=" + companyId + ", companyPid=" + companyPid + ", companyName="
				+ companyName + ", login=" + login + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + ", mobile=" + mobile + ", activated=" + activated + ", langKey=" + langKey + ", deviceKey="
				+ deviceKey + ", authorities=" + authorities + ", showAllUserData=" + showAllUserData + "]";
	}

}
