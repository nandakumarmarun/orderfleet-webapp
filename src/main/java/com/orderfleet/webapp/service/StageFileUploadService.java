package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.StageFileUploadDTO;

public interface StageFileUploadService {

	String PID_PREFIX = "SFU-";
	
	StageFileUploadDTO save(StageFileUploadDTO stageFileUploadDTO);

	StageFileUploadDTO update(StageFileUploadDTO stageFileUploadDTO);
	
	StageFileUploadDTO findOneByPid(String pid);
	
	void delete(String pid);
}
