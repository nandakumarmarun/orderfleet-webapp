package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;

/**
 * A UserTaskGroupDTO for tab.
 * 
 * @author Shaheer
 * @since July 11, 2016
 * 
 * @see UserTaskGroupAssignment
 */
public class UserTaskGroupDTO {
	
	private String pid;
	private String assigner;
	private TaskGroupDTO taskGroup;
	private PriorityStatus priorityStatus;
	private LocalDate startDate;
	private String remarks;
	
	public UserTaskGroupDTO(UserTaskGroupAssignment userTaskGroupAssignment) {
		this(userTaskGroupAssignment.getPid(), userTaskGroupAssignment.getUser().getFirstName(), userTaskGroupAssignment.getTaskGroup(), userTaskGroupAssignment.getPriorityStatus(),
				userTaskGroupAssignment.getStartDate(), userTaskGroupAssignment.getRemarks());
	}
	
	public UserTaskGroupDTO(String pid, String assigner, com.orderfleet.webapp.domain.TaskGroup savedTaskGroup, PriorityStatus priorityStatus,
			LocalDate startDate, String remarks) {
		super();
		this.pid = pid;
		this.assigner = assigner;
		this.taskGroup = new TaskGroupDTO(savedTaskGroup);
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
	
	public TaskGroupDTO getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(TaskGroupDTO taskGroup) {
		this.taskGroup = taskGroup;
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
