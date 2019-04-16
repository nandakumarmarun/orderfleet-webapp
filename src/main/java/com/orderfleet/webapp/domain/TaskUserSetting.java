package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A TaskUserSetting.
 * 
 * @author Shaheer
 * @since October 03, 2016
 */
@Entity
@Table(name = "tbl_task_user_setting")
public class TaskUserSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_task_user_setting_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_task_user_setting_id") })
	@GeneratedValue(generator = "seq_task_user_setting_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_task_user_setting_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	private User executor;

	@NotNull
	@ManyToOne
	private TaskSetting taskSetting;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_task_user_setting_approvers", joinColumns = {
			@JoinColumn(name = "task_user_setting_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "user_id", referencedColumnName = "id") })
	private List<User> approvers;

	@NotNull
	@ManyToOne
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

	public TaskSetting getTaskSetting() {
		return taskSetting;
	}

	public void setTaskSetting(TaskSetting taskSetting) {
		this.taskSetting = taskSetting;
	}

	public User getExecutor() {
		return executor;
	}

	public void setExecutor(User executor) {
		this.executor = executor;
	}

	public List<User> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<User> approvers) {
		this.approvers = approvers;
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
		TaskUserSetting taskUserSetting = (TaskUserSetting) o;
		if (taskUserSetting.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, taskUserSetting.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TaskUserSetting{" + "id=" + id + ", executor='" + executor.getLogin() + "'" + '}';
	}

}
