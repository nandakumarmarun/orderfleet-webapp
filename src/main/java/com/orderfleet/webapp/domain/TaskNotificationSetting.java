package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.ActivityEvent;

/**
 * A TaskNotificationSetting.
 */
@Entity
@Table(name = "tbl_task_notification_setting")
public class TaskNotificationSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_task_notification_setting_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_task_notification_setting_id") })
	@GeneratedValue(generator = "seq_task_notification_setting_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_task_notification_setting_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	private Activity activity;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "activity_event", nullable = false)
	private ActivityEvent activityEvent;

	@Column(name = "script", length = 15000)
	private String script;

	@ElementCollection
	@CollectionTable(name="tbl_task_notification_setting_Column",joinColumns=@JoinColumn(name="task_notification_setting_id"))
	private Collection<TaskNotificationSettingColumn> taskNotificationSettingColumns = new ArrayList<>();

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

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public ActivityEvent getActivityEvent() {
		return activityEvent;
	}

	public void setActivityEvent(ActivityEvent activityEvent) {
		this.activityEvent = activityEvent;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Collection<TaskNotificationSettingColumn> getTaskNotificationSettingColumns() {
		return taskNotificationSettingColumns;
	}

	public void setTaskNotificationSettingColumns(
			Collection<TaskNotificationSettingColumn> taskNotificationSettingColumns) {
		this.taskNotificationSettingColumns = taskNotificationSettingColumns;
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
		TaskNotificationSetting taskNotificationSetting = (TaskNotificationSetting) o;
		if (taskNotificationSetting.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, taskNotificationSetting.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TaskNotificationSetting{" + "id=" + id + ", activityEvent='" + activityEvent + "'" + '}';
	}

}
