package com.orderfleet.webapp.web.vendor.integre.dto;

import java.util.Base64;
import java.util.Objects;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class EmployeeProfileVendorDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String code;
	
	private String referenceId;

	private String address;

	@Size(max = 20)
	private String phone;

	@Size(max = 100)
	private String email;

	@Size(max = 5000000)
	@Lob
	private byte[] profileImage;

	private String profileImageContentType;

	private String encodedBase64Image;

	private String designationPid;

	private String designationName;

	private String departmentPid;

	private String departmentName;

	private String userFirstName;

	private String userLastName;

	private String userPid;

	private boolean activated;

	
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

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public String getProfileImageContentType() {
		return profileImageContentType;
	}

	public void setProfileImageContentType(String profileImageContentType) {
		this.profileImageContentType = profileImageContentType;
	}

	public String getDesignationPid() {
		return designationPid;
	}

	public void setDesignationPid(String designationPid) {
		this.designationPid = designationPid;
	}

	public String getDesignationName() {
		return designationName;
	}

	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}

	public String getDepartmentPid() {
		return departmentPid;
	}

	public void setDepartmentPid(String departmentPid) {
		this.departmentPid = departmentPid;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	public String getEncodedBase64Image() {
		return encodedBase64Image;
	}

	public void setEncodedBase64Image(byte[] image) {
		String encodedImage = Base64.getEncoder().encodeToString(image);
		this.encodedBase64Image = encodedImage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		EmployeeProfileVendorDTO employeeProfileVendorDTO = (EmployeeProfileVendorDTO) o;

		if (!Objects.equals(pid, employeeProfileVendorDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}
}
