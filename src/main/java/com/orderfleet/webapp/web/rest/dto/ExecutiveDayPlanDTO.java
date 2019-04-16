package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A ExecutiveDayPlanDTO
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public class ExecutiveDayPlanDTO {

	private List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs;
	private List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlanDTOs;
	private List<ExecutiveTaskListPlanDTO> executiveTaskListPlanDTOs;

	public ExecutiveDayPlanDTO() {
		super();
	}

	public List<ExecutiveTaskPlanDTO> getExecutiveTaskPlanDTOs() {
		return executiveTaskPlanDTOs;
	}

	public void setExecutiveTaskPlanDTOs(List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs) {
		this.executiveTaskPlanDTOs = executiveTaskPlanDTOs;
	}

	public List<ExecutiveTaskGroupPlanDTO> getExecutiveTaskGroupPlanDTOs() {
		return executiveTaskGroupPlanDTOs;
	}

	public void setExecutiveTaskGroupPlanDTOs(List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlanDTOs) {
		this.executiveTaskGroupPlanDTOs = executiveTaskGroupPlanDTOs;
	}

	public List<ExecutiveTaskListPlanDTO> getExecutiveTaskListPlanDTOs() {
		return executiveTaskListPlanDTOs;
	}

	public void setExecutiveTaskListPlanDTOs(List<ExecutiveTaskListPlanDTO> executiveTaskListPlanDTOs) {
		this.executiveTaskListPlanDTOs = executiveTaskListPlanDTOs;
	}

	@Override
	public String toString() {
		return "ExecutiveDayPlanDTO [executiveTaskPlanDTOs=" + executiveTaskPlanDTOs + ", executiveTaskGroupPlanDTOs="
				+ executiveTaskGroupPlanDTOs + ", executiveTaskListPlanDTOs=" + executiveTaskListPlanDTOs + "]";
	}

}
