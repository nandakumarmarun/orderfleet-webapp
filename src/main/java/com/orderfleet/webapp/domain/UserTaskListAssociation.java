package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "tbl_user_task_list_association")
public class UserTaskListAssociation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_task_list_association_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_task_list_association_id") })
	@GeneratedValue(generator = "seq_user_task_list_association_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_task_list_association_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@NotNull
	private User user;



	@Column(name = "task_list_name", unique = true, nullable = false, updatable = false)
	private String taskListName;

	@Column(name = "task_list_id", unique = true, nullable = false, updatable = false)
	private String taskListId;

	@ManyToOne
	@NotNull
	private Company company;

	public UserTaskListAssociation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserTaskListAssociation(Long id, String pid, User user, String taskListName, String taskListId, Company company) {
		this.id = id;
		this.pid = pid;
		this.user = user;
		this.taskListName = taskListName;
		this.taskListId = taskListId;
		this.company = company;
	}

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTaskListName() {
		return taskListName;
	}

	public void setTaskListName(String taskListName) {
		this.taskListName = taskListName;
	}

	public String getTaskListId() {
		return taskListId;
	}

	public void setTaskListId(String taskListId) {
		this.taskListId = taskListId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "UserTaskListAssociation{" +
				"id=" + id +
				", pid='" + pid + '\'' +
				", user=" + user +
				", taskListName='" + taskListName + '\'' +
				", taskListId='" + taskListId + '\'' +
				", company=" + company +
				'}';
	}
}
