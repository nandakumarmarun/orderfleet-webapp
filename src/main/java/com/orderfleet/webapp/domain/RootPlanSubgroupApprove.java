package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Domain for managing RootPlanSubgroupApprove.
 * 
 * @author fahad
 * @since Aug 28, 2017
 */
@Entity
@Table(name = "tbl_root_plan_subgroup_approve")
public class RootPlanSubgroupApprove implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_root_plan_subgroup_approve_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_root_plan_subgroup_approve_id") })
	@GeneratedValue(generator = "seq_root_plan_subgroup_approve_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_root_plan_subgroup_approve_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;
	
	@ManyToOne
	@NotNull
	private AttendanceStatusSubgroup attendanceStatusSubgroup;
	
	@NotNull
	@Column(name = "approval_required", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean approvalRequired;
	
	@NotNull
	@Column(name = "root_plan_based", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean rootPlanBased;
	
	@NotNull
	@ManyToOne
	private User user;

	
	public RootPlanSubgroupApprove() {
		super();
	}

	public RootPlanSubgroupApprove(Long id, Company company, AttendanceStatusSubgroup attendanceStatusSubgroup,
			boolean approvalRequired, boolean rootPlanBased, User user) {
		super();
		this.id = id;
		this.company = company;
		this.attendanceStatusSubgroup = attendanceStatusSubgroup;
		this.approvalRequired = approvalRequired;
		this.rootPlanBased = rootPlanBased;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public AttendanceStatusSubgroup getAttendanceStatusSubgroup() {
		return attendanceStatusSubgroup;
	}

	public void setAttendanceStatusSubgroup(AttendanceStatusSubgroup attendanceStatusSubgroup) {
		this.attendanceStatusSubgroup = attendanceStatusSubgroup;
	}

	public boolean getApprovalRequired() {
		return approvalRequired;
	}

	public void setApprovalRequired(boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}

	public boolean getRootPlanBased() {
		return rootPlanBased;
	}

	public void setRootPlanBased(boolean rootPlanBased) {
		this.rootPlanBased = rootPlanBased;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "RootPlanSubgroupApprove [id=" + id + ", company=" + company + ", attendanceStatusSubgroup="
				+ attendanceStatusSubgroup + ", approvalRequired=" + approvalRequired + ", rootPlanBased="
				+ rootPlanBased + ", user=" + user + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RootPlanSubgroupApprove rootPlanSubgroupApprove = (RootPlanSubgroupApprove) o;
		if (rootPlanSubgroupApprove.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, rootPlanSubgroupApprove.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
}
