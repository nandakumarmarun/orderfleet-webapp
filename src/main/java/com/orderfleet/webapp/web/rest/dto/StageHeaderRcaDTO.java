package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.StageHeaderRca;

public class StageHeaderRcaDTO {

	private StageHeaderDTO stageHeader;
	
	private RootCauseAnalysisReasonDTO rca;

	public StageHeaderRcaDTO() {
		super();
	}

	

	public StageHeaderRcaDTO(StageHeaderRca stageHeaderRca) {
		super();
		this.stageHeader = new StageHeaderDTO(stageHeaderRca.getStageHeader());
		this.rca = new RootCauseAnalysisReasonDTO(stageHeaderRca.getRootCauseAnalysisReason());
	}



	public StageHeaderDTO getStageHeader() {
		return stageHeader;
	}

	public void setStageHeader(StageHeaderDTO stageHeader) {
		this.stageHeader = stageHeader;
	}

	public RootCauseAnalysisReasonDTO getRca() {
		return rca;
	}

	public void setRca(RootCauseAnalysisReasonDTO rca) {
		this.rca = rca;
	}
	
	
}
