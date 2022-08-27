package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class PendingTaskView {
	
	private String pid;
	
	private String empName;
	
	private String userLogin;
	
	private String territory;
	
	private long unattendedTask;
	
	private List<TaskPlan> taskList;

	public PendingTaskView() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PendingTaskView(String empName, String userLogin, String territory, long unattendedTask,
			List<TaskPlan> taskList) {
		super();
		this.empName = empName;
		this.userLogin = userLogin;
		this.territory = territory;
		this.unattendedTask = unattendedTask;
		this.taskList = taskList;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

	public long getUnattendedTask() {
		return unattendedTask;
	}

	public void setUnattendedTask(long unattendedTask) {
		this.unattendedTask = unattendedTask;
	}

	public List<TaskPlan> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<TaskPlan> taskList) {
		this.taskList = taskList;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public String toString() {
		return "PendingTaskView [empName=" + empName + ", userLogin=" + userLogin + ", territory=" + territory
				+ ", unattendedTask=" + unattendedTask + ", taskList=" + taskList + "]";
	} 
	
	

}
