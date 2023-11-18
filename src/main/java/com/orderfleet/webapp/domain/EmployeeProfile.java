package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

/**
 * A EmployeeProfile.
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
@Entity
@Table(name = "tbl_employee_profile")
public class EmployeeProfile implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_employee_profile_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_employee_profile_id") })
	@GeneratedValue(generator = "seq_employee_profile_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_employee_profile_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;
	
	@Column(name = "employee_profile_id")
	private String employeeProfileId;

	@Column(name = "employee_profile_code")
	private String employeeProfileCode;
	
	@Column(name = "org_emp_Id")
	private String orgEmpId;
	

	// id of third party system like rosh
	@Column(name = "reference_id")
	private String referenceId;

	@NotNull
	@Size(max = 500)
	@Column(name = "address", length = 500, nullable = false)
	private String address;

	@Size(max = 20)
	@Column(name = "phone", length = 20)
	private String phone;

	@Email
	@Size(max = 100)
	@Column(name = "email", length = 100)
	private String email;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private ZonedDateTime createdDate;

	@Column(name = "last_updated_date")
	private ZonedDateTime lastUpdatedDate;

	@Size(max = 5000000)
	@Lob
	@Column(name = "profile_image")
	private byte[] profileImage;

	@Column(name = "profile_image_content_type")
	private String profileImageContentType;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private Designation designation;

	@ManyToOne
	@NotNull
	private Department department;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@NotNull
	@Column(name = "battery_percentage", nullable = false, columnDefinition = "integer default 0")
	private Integer batteryPercentage = 0;
	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@PrePersist
	protected void onCreate() {
		lastUpdatedDate = createdDate = ZonedDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		lastUpdatedDate = ZonedDateTime.now();
	}

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
	

	public String getEmployeeProfileId() {
		return employeeProfileId;
	}

	public void setEmployeeProfileId(String employeeProfileId) {
		this.employeeProfileId = employeeProfileId;
	}

	public String getEmployeeProfileCode() {
		return employeeProfileCode;
	}

	public void setEmployeeProfileCode(String employeeProfileCode) {
		this.employeeProfileCode = employeeProfileCode;
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
	
	

	public String getOrgEmpId() {
		return orgEmpId;
	}

	public void setOrgEmpId(String orgEmpId) {
		this.orgEmpId = orgEmpId;
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

	public ZonedDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public ZonedDateTime getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(ZonedDateTime lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	/*
	 * public Set<Location> getLocations() { return locations; }
	 * 
	 * public void setLocations(Set<Location> locations) { this.locations =
	 * locations; }
	 */

	public Designation getDesignation() {
		return designation;
	}

	public void setDesignation(Designation designation) {
		this.designation = designation;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getBatteryPercentage() {
		return batteryPercentage;
	}

	public void setBatteryPercentage(Integer batteryPercentage) {
		this.batteryPercentage = batteryPercentage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EmployeeProfile employeeProfile = (EmployeeProfile) o;
		if (employeeProfile.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, employeeProfile.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "EmployeeProfile [id=" + id + ", pid=" + pid + ", name=" + name + ", alias=" + alias
				+ ", employeeProfileId=" + employeeProfileId + ", employeeProfileCode=" + employeeProfileCode
				+ ", orgEmpId=" + orgEmpId + ", referenceId=" + referenceId + ", address=" + address + ", phone="
				+ phone + ", email=" + email + ", createdDate=" + createdDate + ", lastUpdatedDate=" + lastUpdatedDate
				+ ", profileImage=" + Arrays.toString(profileImage) + ", profileImageContentType="
				+ profileImageContentType + ", company=" + company + ", designation=" + designation + ", department="
				+ department + ", user=" + user + ", activated=" + activated + "]";
	}



}
