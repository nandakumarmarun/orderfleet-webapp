package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A TaskGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Entity
@Table(name = "tbl_task_group")
public class TaskGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_task_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_task_group_id") })
	@GeneratedValue(generator = "seq_task_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_task_group_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private ActivityGroup activityGroup;

	@ManyToMany
	@JoinTable(name = "tbl_task_group_task", joinColumns = @JoinColumn(name = "task_group_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "ID"))
	private Set<Task> tasks = new HashSet<>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ActivityGroup getActivityGroup() {
		return activityGroup;
	}

	public void setActivityGroup(ActivityGroup activityGroup) {
		this.activityGroup = activityGroup;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TaskGroup taskGroup = (TaskGroup) o;
		if (taskGroup.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, taskGroup.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "TaskGroup{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", alias='" + alias + "'"
				+ ", description='" + description + "'" + '}';
	}
}
