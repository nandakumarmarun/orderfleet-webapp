package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;

/**
 * A ExecutiveTaskPlan.
 */
@Entity
@Table(name = "tbl_executive_task_plan")
public class ExecutiveTaskPlan implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_executive_task_plan_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_executive_task_plan_id") })
	@GeneratedValue(generator = "seq_executive_task_plan_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_executive_task_plan_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@NotNull
	@Column(name = "created_by", nullable = false, length = 50, updatable = false)
	private String createdBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "task_plan_status", nullable = false, columnDefinition = "character varying DEFAULT 'PENDING' ")
	private TaskPlanStatus taskPlanStatus;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "user_remarks")
	private String userRemarks;

	@NotNull
	@Column(name = "planned_date", nullable = false)
	private LocalDateTime plannedDate;

	@NotNull
	@ManyToOne
	private Company company;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Activity activity;

	@NotNull
	@ManyToOne
	private AccountType accountType;

	@NotNull
	@ManyToOne
	private AccountProfile accountProfile;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "task_created_type", nullable = false, columnDefinition = "character varying DEFAULT 'TASK_SERVER' ")
	private TaskCreatedType taskCreatedType;

	@ManyToOne
	private Task task;

	@ManyToOne
	private TaskGroup taskGroup;

	@ManyToOne
	private TaskList taskList;

	@NotNull
	@Column(name = "sort_order", nullable = false, columnDefinition = "integer DEFAULT 0 ")
	private int sortOrder;
	
	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public TaskPlanStatus getTaskPlanStatus() {
		return taskPlanStatus;
	}

	public void setTaskPlanStatus(TaskPlanStatus taskPlanStatus) {
		this.taskPlanStatus = taskPlanStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
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

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public TaskCreatedType getTaskCreatedType() {
		return taskCreatedType;
	}

	public void setTaskCreatedType(TaskCreatedType taskCreatedType) {
		this.taskCreatedType = taskCreatedType;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getUserRemarks() {
		return userRemarks;
	}

	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExecutiveTaskPlan executiveTaskPlan = (ExecutiveTaskPlan) o;
		if (executiveTaskPlan.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, executiveTaskPlan.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "ExecutiveTaskPlan{" + "id=" + id + ", pid='" + pid + "'" + createdDate + "'" + ", remarks='" + remarks
				+ "'" + ", plannedDate='" + plannedDate + "'" + '}';
	}
}
