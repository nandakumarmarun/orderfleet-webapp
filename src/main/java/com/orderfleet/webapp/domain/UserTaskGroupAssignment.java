package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.PriorityStatus;

/**
 * A UserTaskGroupAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
@Entity
@Table(name = "tbl_user_task_group_assignment")
public class UserTaskGroupAssignment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_task_group_assignment_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_task_group_assignment_id") })
	@GeneratedValue(generator = "seq_user_task_group_assignment_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_task_group_assignment_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	@NotNull
	private User executiveUser;

	@ManyToOne
	@NotNull
	private TaskGroup taskGroup;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "priority_status", nullable = false)
	private PriorityStatus priorityStatus;

	@NotNull
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "remarks")
	private String remarks;

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

	public User getExecutiveUser() {
		return executiveUser;
	}

	public void setExecutiveUser(User executiveUser) {
		this.executiveUser = executiveUser;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public PriorityStatus getPriorityStatus() {
		return priorityStatus;
	}

	public void setPriorityStatus(PriorityStatus priorityStatus) {
		this.priorityStatus = priorityStatus;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserTaskGroupAssignment userTaskAssignment = (UserTaskGroupAssignment) o;
		if (userTaskAssignment.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userTaskAssignment.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "UserTaskGroupAssignment{" + "id=" + id + ", pid='" + pid + "'" + ", priorityStatus='" + priorityStatus
				+ "'" + '}';
	}
}
