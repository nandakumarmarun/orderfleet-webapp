package com.orderfleet.webapp.service;

import java.util.Optional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.BestPerformer;
import com.orderfleet.webapp.web.rest.dto.BestPerformerUploadDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;

public interface BestPerformerService {


	String PID_PREFIX = "BEST-";
	

	Optional<BestPerformerUploadDTO> findOneByPid(String pid);
	BestPerformerUploadDTO update(BestPerformerUploadDTO bestPerformerUploadDTO);
}
