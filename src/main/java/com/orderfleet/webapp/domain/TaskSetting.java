package com.orderfleet.webapp.domain;

import java.io.Serializable;
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

import com.orderfleet.webapp.domain.enums.ActivityEvent;

/**
 * A TaskSetting.
 * 
 * @author Shaheer
 * @since October 03, 2016
 */
@Entity
@Table(name = "tbl_task_setting")
public class TaskSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_task_setting_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_task_setting_id") })
	@GeneratedValue(generator = "seq_task_setting_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_task_setting_id')")
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

	@NotNull
	@ManyToOne
	private Activity taskActivity;

	@Column(name = "form_element_pid")
	private String formElementPid;

	@Column(name = "start_date_column")
	private String startDateColumn;

	@Column(name = "script", length = 15000)
	private String script;

	@Column(name = "required", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE' ")
	private boolean required;
	
	@Column(name = "create_plan", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE' ")
	private boolean createPlan;

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

	public Activity getTaskActivity() {
		return taskActivity;
	}

	public void setTaskActivity(Activity taskActivity) {
		this.taskActivity = taskActivity;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
	}

	public String getStartDateColumn() {
		return startDateColumn;
	}

	public void setStartDateColumn(String startDateColumn) {
		this.startDateColumn = startDateColumn;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean getCreatePlan() {
		return createPlan;
	}

	public void setCreatePlan(boolean createPlan) {
		this.createPlan = createPlan;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TaskSetting taskSetting = (TaskSetting) o;
		if (taskSetting.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, taskSetting.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TaskSetting{" + "id=" + id + ", activityEvent='" + activityEvent + "'" + '}';
	}

}
