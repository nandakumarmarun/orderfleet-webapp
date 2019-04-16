package com.orderfleet.webapp.service.impl;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.StageFileUpload;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StageFileUploadRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StageFileUploadService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.StageFileUploadDTO;

@Service
@Transactional
public class StageFileUploadServiceImpl implements StageFileUploadService{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private StageFileUploadRepository stageFileUploadRepository;
	 
	@Inject
	private StageRepository stageRepository;

	@Override
	public StageFileUploadDTO save(StageFileUploadDTO stageFileUploadDTO) {
		StageFileUpload stageFileUpload = new StageFileUpload();
		stageFileUpload.setPid(StageFileUploadService.PID_PREFIX + RandomUtil.generatePid());
		stageFileUpload.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		stageFileUpload.setFileUploadName(stageFileUploadDTO.getFileUploadName());
		stageFileUpload.setStage(stageRepository.findOneByPid(stageFileUploadDTO.getStagePid()).get());
		StageFileUploadDTO result = new StageFileUploadDTO(stageFileUploadRepository.save(stageFileUpload));
		return result;
	}

	@Override
	public StageFileUploadDTO update(StageFileUploadDTO stageFileUploadDTO) {
		Optional<StageFileUpload> stageFileUploadExist = stageFileUploadRepository.findOneByPid(stageFileUploadDTO.getPid());
		if(stageFileUploadExist.isPresent()){
			StageFileUpload stageFileUpload = stageFileUploadExist.get();
			stageFileUpload.setFileUploadName(stageFileUploadDTO.getFileUploadName());
			stageFileUpload.setStage(stageRepository.findOneByPid(stageFileUploadDTO.getStagePid()).get());
			StageFileUploadDTO result = new StageFileUploadDTO(stageFileUploadRepository.save(stageFileUpload));
			return result;
		}else{
			return null;
		}
	}

	@Override
	public StageFileUploadDTO findOneByPid(String pid) {
		Optional<StageFileUpload> stageFileUploadExist = stageFileUploadRepository.findOneByPid(pid);
		if(stageFileUploadExist.isPresent()){
			StageFileUploadDTO result = new StageFileUploadDTO(stageFileUploadExist.get());
			return result;
		}else{
			return null;
		}
	}

	@Override
	public void delete(String pid) {
			log.debug("Request to delete StageFileUpload : {}", pid);
			stageFileUploadRepository.findOneByPid(pid).ifPresent(stageFileUpload -> {
				stageFileUploadRepository.delete(stageFileUpload.getId());
			});
	}
}
