package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.StageGroup;
import com.orderfleet.webapp.web.rest.dto.StageGroupDTO;

public interface StageGroupService {

	String PID_PREFIX = "STGGRP-";

	StageGroupDTO save(StageGroupDTO stageGroupDto);

	StageGroupDTO update(StageGroupDTO stageGroupDto);
	
	StageGroup findOne(Long id);

	Optional<StageGroup> findOneByPid(String pid);

	Optional<StageGroup> findByName(String name);

	void delete(String pid);
	
	StageGroupDTO updateStageGroupStatus(String pid, boolean activate);
	
	List<StageGroup> findAllByCompany();

	List<StageGroup> findAllByCompanyAndActivatedTrue();
	
	List<StageGroup> findAllByCompanyAndActivated(boolean activated);
}
