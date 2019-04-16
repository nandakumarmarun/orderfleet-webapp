package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;

/**
 * A DTO for the Partner entity.
 *
 * @author Sarath
 * @since Feb 14, 2018
 *
 */
public class PartnerDTO {
	private String pid;
	private String firstName;
	private String lastName;
	private String address;
	private String country;
	private String countryCode;
	private String state;
	private String stateCode;
	private String district;
	private String districtCode;
	private String email;
	private String pincode;
	private String location;
	private Boolean activated;
	private String mobile;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	private String login;

	public PartnerDTO() {
		super();
	}

	public PartnerDTO(ManagedUserDTO managedUserDTO) {
		super();
		this.pid = managedUserDTO.getPid();
		this.firstName = managedUserDTO.getFirstName();
		this.lastName = managedUserDTO.getLastName();
		this.mobile = managedUserDTO.getMobile();
		this.login = managedUserDTO.getLogin();
		this.activated = managedUserDTO.isActivated();
	}

	public PartnerDTO(User user) {
		super();
		this.pid = user.getPid();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.mobile = user.getMobile();
		this.login = user.getLogin();
		this.activated = user.getActivated();

		this.address = user.getCompany().getAddress1();
		this.country = user.getCompany().getCountry().getName();
		this.state = user.getCompany().getState().getName();
		this.district = user.getCompany().getDistrict().getName();
		this.countryCode = user.getCompany().getCountry().getCode();
		this.stateCode = user.getCompany().getState().getCode();
		this.districtCode = user.getCompany().getDistrict().getCode();
		this.email = user.getCompany().getEmail();
		this.pincode = user.getCompany().getPincode();
		this.location = user.getCompany().getLocation();

	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
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

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String toString() {
		return "PartnerDTO [pid=" + pid + ", firstName=" + firstName + ", lastName=" + lastName + ", address=" + address
				+ ", country=" + country + ", countryCode=" + countryCode + ", state=" + state + ", stateCode="
				+ stateCode + ", district=" + district + ", districtCode=" + districtCode + ", email=" + email
				+ ", pincode=" + pincode + ", location=" + location + ", activated=" + activated + ", mobile=" + mobile
				+ ", login=" + login + "]";
	}

}
