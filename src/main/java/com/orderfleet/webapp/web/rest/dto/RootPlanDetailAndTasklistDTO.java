package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class RootPlanDetailAndTasklistDTO {

	private List<TaskListDTO> taskListDTOs;

	private List<RootPlanDetailDTO> rootPlanDetailDTOs;

	public RootPlanDetailAndTasklistDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RootPlanDetailAndTasklistDTO(List<TaskListDTO> taskListDTOs, List<RootPlanDetailDTO> rootPlanDetailDTOs) {
		super();
		this.taskListDTOs = taskListDTOs;
		this.rootPlanDetailDTOs = rootPlanDetailDTOs;
	}

	public List<TaskListDTO> getTaskListDTOs() {
		return taskListDTOs;
	}

	public void setTaskListDTOs(List<TaskListDTO> taskListDTOs) {
		this.taskListDTOs = taskListDTOs;
	}

	public List<RootPlanDetailDTO> getRootPlanDetailDTOs() {
		return rootPlanDetailDTOs;
	}

	public void setRootPlanDetailDTOs(List<RootPlanDetailDTO> rootPlanDetailDTOs) {
		this.rootPlanDetailDTOs = rootPlanDetailDTOs;
	}

	@Override
	public String toString() {
		return "RootPlanDetailAndTasklistDTO [taskListDTOs=" + taskListDTOs + ", rootPlanDetailDTOs="
				+ rootPlanDetailDTOs + "]";
	}

}
