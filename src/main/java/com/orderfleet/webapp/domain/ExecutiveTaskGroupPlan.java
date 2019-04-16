package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
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
 * A ExecutiveTaskGroupPlan.
 * 
 * @author Sarath
 * @since July 14, 2016
 */
@Entity
@Table(name = "tbl_executive_task_group_plan")
public class ExecutiveTaskGroupPlan implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_executive_task_group_plan_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_executive_task_group_plan_id") })
	@GeneratedValue(generator = "seq_executive_task_group_plan_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_executive_task_group_plan_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "planned_date", nullable = false)
	private LocalDateTime plannedDate;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "remarks")
	private String remarks;

	@ManyToOne
	@NotNull
	private TaskGroup taskGroup;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	@NotNull
	private Company company;

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

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
		ExecutiveTaskGroupPlan executiveTaskGroupPlan = (ExecutiveTaskGroupPlan) o;
		if (executiveTaskGroupPlan.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, executiveTaskGroupPlan.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "ExecutiveTaskGroupPlan{" + "id=" + id + ", plannedDate='" + plannedDate + "'" + ", createdDate='"
				+ createdDate + "'" + ", remarks='" + remarks + "'" + '}';
	}
}
