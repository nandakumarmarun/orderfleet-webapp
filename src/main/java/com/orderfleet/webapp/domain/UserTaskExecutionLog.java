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
 * A UserTaskExecutionLog.
 * 
 * @author Muhammed Riyas T
 * @since 11 Novembor, 2016
 */
@Entity
@Table(name = "tbl_user_task_execution_log")
public class UserTaskExecutionLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_task_execution_log_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_task_execution_log_id") })
	@GeneratedValue(generator = "seq_user_task_execution_log_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_task_execution_log_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private UserTaskAssignment userTaskAssignment;

	@NotNull
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@ManyToOne
	private Company company;

	public UserTaskExecutionLog() {

	}

	public UserTaskExecutionLog(UserTaskAssignment userTaskAssignment, ExecutiveTaskExecution executiveTaskExecution,
			Company company) {
		super();
		this.userTaskAssignment = userTaskAssignment;
		this.executiveTaskExecution = executiveTaskExecution;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserTaskAssignment getUserTaskAssignment() {
		return userTaskAssignment;
	}

	public void setUserTaskAssignment(UserTaskAssignment userTaskAssignment) {
		this.userTaskAssignment = userTaskAssignment;
	}

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
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
		UserTaskExecutionLog accountGroup = (UserTaskExecutionLog) o;
		if (accountGroup.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, accountGroup.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
