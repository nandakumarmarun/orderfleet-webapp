package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.RootCauseAnalysisReasonDTO;

public interface RootCauseAnalysisReasonService {

	String PID_PREFIX = "RCAR-";

	RootCauseAnalysisReasonDTO save(RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO);
	
	RootCauseAnalysisReasonDTO update(RootCauseAnalysisReasonDTO rootCauseAnalysisReasonDTO);
	
	void delete(String pid);
	
	Optional<RootCauseAnalysisReasonDTO> findOneByPid(String pid);
	
	List<RootCauseAnalysisReasonDTO> findAllByCompany();
}
