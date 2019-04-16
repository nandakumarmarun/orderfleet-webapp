package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orderfleet.webapp.domain.StageDetail;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StageDetailDTO {
	
	public StageDetailDTO(StageDetail stageDetail) {
	}
	

}
