package com.orderfleet.webapp.web.rest.api.dto;

import java.util.HashSet;
import java.util.Set;

import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

public class RootPlanHeaderUserTaskListDTO {
	
	private Long id;
	
	private String name;
	
	private Set<TaskDTO> tasks = new HashSet<>();
	
	private ApprovalStatus approvalStatus;
	
	private Long rootPlanOrder;

	
	
	public RootPlanHeaderUserTaskListDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RootPlanHeaderUserTaskListDTO(Long id,String name, Set<TaskDTO> tasks, ApprovalStatus approvalStatus,
			Long rootPlanOrder) {
		super();
		this.id=id;
		this.name = name;
		this.tasks = tasks;
		this.approvalStatus = approvalStatus;
		this.rootPlanOrder = rootPlanOrder;
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<TaskDTO> getTasks() {
		return tasks;
	}

	public void setTasks(Set<TaskDTO> tasks) {
		this.tasks = tasks;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public Long getRootPlanOrder() {
		return rootPlanOrder;
	}

	public void setRootPlanOrder(Long rootPlanOrder) {
		this.rootPlanOrder = rootPlanOrder;
	}

	@Override
	public String toString() {
		return "RootPlanHeaderUserTaskListDTO [id=" + id + ", name=" + name + ", tasks=" + tasks + ", approvalStatus="
				+ approvalStatus + ", rootPlanOrder=" + rootPlanOrder + "]";
	}

}
