package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

/**
 * A Muhammed Riyas T
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
@Entity
@Table(name = "tbl_employee_profile_history")
public class EmployeeProfileHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_employee_profile_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_employee_profile_history_id") })
	@GeneratedValue(generator = "seq_employee_profile_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_employee_profile_history_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, unique = true, nullable = false)
	private String name;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;

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

	@PrePersist
	protected void onCreate() {
		createdDate = ZonedDateTime.now();
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

	public ZonedDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EmployeeProfileHistory employeeProfile = (EmployeeProfileHistory) o;
		if (employeeProfile.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, employeeProfile.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
