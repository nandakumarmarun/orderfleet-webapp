package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

/**
 * A UserTaskDTO for tab.
 * 
 * @author Shaheer
 * @since July 11, 2016
 * 
 * @see UserTaskAssignment
 */
public class UserTaskDTO {
	
	private String pid;
	private String assigner;
	private TaskDTO task;
	private PriorityStatus priorityStatus;
	private LocalDate startDate;
	private String remarks;
	private String taskRefDocumentPid;
	
	public UserTaskDTO(UserTaskAssignment userTaskAssignment) {
		this(userTaskAssignment.getPid(), userTaskAssignment.getUser().getFirstName(), userTaskAssignment.getTask(),
				userTaskAssignment.getPriorityStatus(), userTaskAssignment.getStartDate(),
				userTaskAssignment.getRemarks());
	}
	
	public UserTaskDTO(UserTaskAssignment userTaskAssignment, String taskRefDocumentPid) {
		this(userTaskAssignment.getPid(), userTaskAssignment.getUser().getFirstName(), userTaskAssignment.getTask(),
				userTaskAssignment.getPriorityStatus(), userTaskAssignment.getStartDate(),
				userTaskAssignment.getRemarks());
		this.taskRefDocumentPid = taskRefDocumentPid;
	}
	
	public UserTaskDTO(String pid, String assigner, com.orderfleet.webapp.domain.Task savedTask,PriorityStatus priorityStatus, LocalDate startDate,
			String remarks) {
		super();
		this.pid = pid;
		this.assigner = assigner;
		this.task = new TaskDTO(savedTask);
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
	
	public PriorityStatus getPriorityStatus() {
		return priorityStatus;
	}
	
	public void setPriorityStatus(PriorityStatus priorityStatus) {
		this.priorityStatus = priorityStatus;
	}

	public TaskDTO getTask() {
		return task;
	}

	public void setTask(TaskDTO task) {
		this.task = task;
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

	public String getTaskRefDocumentPid() {
		return taskRefDocumentPid;
	}

	public void setTaskRefDocumentPid(String taskRefDocumentPid) {
		this.taskRefDocumentPid = taskRefDocumentPid;
	}
	
}
