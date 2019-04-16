package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;

/**
 * A UserTaskListDTO for tab.
 * 
 * @author Sarath
 * @since July 15, 2016
 */
public class UserTaskListDTO {

	private String pid;
	private String assigner;
	private TaskListDTO taskList;
	private PriorityStatus priorityStatus;
	private LocalDate startDate;
	private String remarks;

	public UserTaskListDTO(UserTaskListAssignment userTaskListAssignment) {
		this(userTaskListAssignment.getPid(), userTaskListAssignment.getUser().getFirstName(), userTaskListAssignment
				.getTaskList(), userTaskListAssignment.getPriorityStatus(), userTaskListAssignment.getStartDate(),
				userTaskListAssignment.getRemarks());
	}

	public UserTaskListDTO(String pid, String assigner, com.orderfleet.webapp.domain.TaskList savedTaskList,
			PriorityStatus priorityStatus, LocalDate startDate, String remarks) {
		super();
		this.pid = pid;
		this.assigner = assigner;
		this.taskList = new TaskListDTO(savedTaskList);
		this.priorityStatus = priorityStatus;
		this.startDate = startDate;
		this.remarks = remarks;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAssigner() {
		return assigner;
	}

	public void setAssigner(String assigner) {
		this.assigner = assigner;
	}

	public TaskListDTO getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskListDTO taskList) {
		this.taskList = taskList;
	}

	public PriorityStatus getPriorityStatus() {
		return priorityStatus;
	}

	public void setPriorityStatus(PriorityStatus priorityStatus) {
		this.priorityStatus = priorityStatus;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
