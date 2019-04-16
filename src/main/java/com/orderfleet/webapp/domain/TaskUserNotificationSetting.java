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
 * A TaskUserNotificationSetting.
 */
@Entity
@Table(name = "tbl_task_user_notification_setting")
public class TaskUserNotificationSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_task_user_notification_setting_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_task_user_notification_setting_id") })
	@GeneratedValue(generator = "seq_task_user_notification_setting_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_task_user_notification_setting_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	private User executor;

	@NotNull
	@ManyToOne
	private TaskNotificationSetting taskNotificationSetting;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_task_user_notification_setting_approvers", joinColumns = {
			@JoinColumn(name = "task_user_notification_setting_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "user_id", referencedColumnName = "id") })
	private List<User> approvers;

	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@Column(name = "enable_territory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableTerritory = false;

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

	public TaskNotificationSetting getTaskNotificationSetting() {
		return taskNotificationSetting;
	}

	public void setTaskNotificationSetting(TaskNotificationSetting taskNotificationSetting) {
		this.taskNotificationSetting = taskNotificationSetting;
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
	
	

	public boolean getEnableTerritory() {
		return enableTerritory;
	}

	public void setEnableTerritory(boolean enableTerritory) {
		this.enableTerritory = enableTerritory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TaskUserNotificationSetting taskUserNotificationSetting = (TaskUserNotificationSetting) o;
		if (taskUserNotificationSetting.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, taskUserNotificationSetting.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TaskUserNotificationSetting{" + "id=" + id + ", executor='" + executor.getLogin() + "'" + '}';
	}

}
