package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.TaskList;

public class UserTaskListTaskDTO {
	
	private String pid;
	
	private String userPid;
	
	private String userName;
	
	private String taskListPid;
	
	private String taskListName;


	
	private TaskList taskList;

	public UserTaskListTaskDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserTaskListTaskDTO(String pid, String userPid, String userName, String taskListPid, String taskListName,
                               TaskList taskList) {
		super();
		this.pid = pid;
		this.userPid = userPid;
		this.userName = userName;
		this.taskListPid = taskListPid;
		this.taskListName = taskListName;
		this.taskList = taskList;

	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTaskListPid() {
		return taskListPid;
	}

	public void setTaskListPid(String taskListPid) {
		this.taskListPid = taskListPid;
	}

	public String getTaskListName() {
		return taskListName;
	}

	public void setTaskListName(String taskListName) {
		this.taskListName = taskListName;
	}

	

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}



	@Override
	public String toString() {
		return "UserTaskListTaskDTO [pid=" + pid + ", userPid=" + userPid + ", userName=" + userName + ", taskListPid="
				+ taskListPid + ", taskListName=" + taskListName + ", taskList=" + taskList + "]";
	}




}
