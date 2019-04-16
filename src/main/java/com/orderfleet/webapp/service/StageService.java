package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.web.rest.dto.StageDTO;

public interface StageService {

	String PID_PREFIX = "STG-";

	StageDTO save(StageDTO stageDto);

	StageDTO update(StageDTO stageDto);
	
	Stage findOne(Long id);

	Optional<Stage> findOneByPid(String pid);

	Optional<Stage> findByName(String name);

	void delete(String pid);
	
	StageDTO updateStageStatus(String pid, boolean activate);
	
	List<Stage> findAllByCompany();

	List<Stage> findAllByCompanyAndActivatedTrue();
	
	List<Stage> findAllByCompanyAndActivated(boolean activated);
}
