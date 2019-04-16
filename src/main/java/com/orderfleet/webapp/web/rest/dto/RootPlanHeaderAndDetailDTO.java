package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class RootPlanHeaderAndDetailDTO {

	List<RootPlanDetailDTO>rootPlanDetailDTOs;
	
	RootPlanHeaderDTO rootPlanHeaderDTO;

	public List<RootPlanDetailDTO> getRootPlanDetailDTOs() {
		return rootPlanDetailDTOs;
	}

	public void setRootPlanDetailDTOs(List<RootPlanDetailDTO> rootPlanDetailDTOs) {
		this.rootPlanDetailDTOs = rootPlanDetailDTOs;
	}

	public RootPlanHeaderDTO getRootPlanHeaderDTO() {
		return rootPlanHeaderDTO;
	}

	public void setRootPlanHeaderDTO(RootPlanHeaderDTO rootPlanHeaderDTO) {
		this.rootPlanHeaderDTO = rootPlanHeaderDTO;
	}
	
	
}
